package com.bolezni.battlecode_course_service_api.sevice.impl;

import com.bolezni.battlecode_course_service_api.dto.task.SaveDraftRequest;
import com.bolezni.battlecode_course_service_api.dto.task.SubmitTaskRequest;
import com.bolezni.battlecode_course_service_api.dto.task.TaskSubmissionInfo;
import com.bolezni.battlecode_course_service_api.mapper.TaskSubmissionMapper;
import com.bolezni.battlecode_course_service_api.model.TaskEntity;
import com.bolezni.battlecode_course_service_api.model.TaskSubmissionEntity;
import com.bolezni.battlecode_course_service_api.repository.PeerReviewRepository;
import com.bolezni.battlecode_course_service_api.repository.TaskSubmissionRepository;
import com.bolezni.battlecode_course_service_api.sevice.TaskService;
import com.bolezni.battlecode_course_service_api.sevice.TaskSubmissionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TaskSubmissionServiceImpl implements TaskSubmissionService {
    TaskSubmissionRepository submissionRepository;
    TaskSubmissionMapper taskSubmissionMapper;
    PeerReviewRepository reviewRepository;
    TaskService taskService;

    @Override
    public TaskSubmissionEntity getById(Long id) {
        return submissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task submission not found with id: " + id));
    }

    @Override
    @Transactional
    public TaskSubmissionEntity save(TaskSubmissionEntity submission) {
        return submissionRepository.save(submission);
    }

    @Override
    public boolean canReview(Long submissionId, String reviewerId) {
        TaskSubmissionEntity submission = getById(submissionId);

        validateNotSameUser(submission.getUserId(), reviewerId, "Cannot review your own submission");

        boolean hasCompletedTask = submissionRepository.existsByTaskIdAndUserIdAndStatus(
                submission.getTask().getId(),
                reviewerId,
                TaskSubmissionEntity.SubmissionStatus.COMPLETED
        );

        if (!hasCompletedTask) {
            return false;
        }

        boolean alreadyReviewed = reviewRepository.existsBySubmissionIdAndReviewerId(
                submissionId,
                reviewerId
        );

        return !alreadyReviewed;
    }

    @Override
    public List<TaskSubmissionInfo> getSubmissionsForReview(String reviewerId, Long taskId) {
        return submissionRepository.findAvailableForReview(
                        taskId,
                        reviewerId,
                        List.of(TaskSubmissionEntity.SubmissionStatus.PUBLISHED, TaskSubmissionEntity.SubmissionStatus.UNDER_REVIEW)
                ).stream()
                .map(taskSubmissionMapper::mapToTaskSubmissionInfo)
                .toList();
    }

    @Override
    public List<TaskSubmissionInfo> getSubmissionsByUser(String userId) {
        return submissionRepository.findByUserId(userId).stream()
                .map(taskSubmissionMapper::mapToTaskSubmissionInfo)
                .toList();
    }

    @Override
    @Transactional
    public TaskSubmissionEntity getOrCreateSubmission(TaskEntity task, String userId, TaskSubmissionEntity.SubmissionStatus status, SaveDraftRequest request) {
        return submissionRepository.findByTaskIdAndUserIdAndStatus(task.getId(), userId, status)
                .orElseGet(() -> {
                    TaskSubmissionEntity newSubmission = TaskSubmissionEntity.builder()
                            .task(task)
                            .userId(userId)
                            .status(status)
                            .completedReviewsCount(0)
                            .requiredReviewsCount(3)
                            .code(request.code())
                            .build();
                    return submissionRepository.save(newSubmission);
                });
    }

    @Override
    public TaskSubmissionInfo getSubmission(String userId, Long submissionId) {
        TaskSubmissionEntity submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> {
                    log.warn("Submission not found: id={}", submissionId);
                    return new EntityNotFoundException("Submission not found with id: " + submissionId);
                });

        validateUserOwnsSubmission(submission, userId);

        return taskSubmissionMapper.mapToTaskSubmissionInfo(submission);
    }

    @Override
    @Transactional
    public TaskSubmissionInfo submitSolution(String userId, SubmitTaskRequest request) {
        log.info("Submitting solution for user={}, taskId={}", userId, request.taskId());

        TaskEntity task = taskService.getById(request.taskId());

        Optional<TaskSubmissionEntity> existingCompleted = submissionRepository
                .findByTaskIdAndUserIdAndStatus(
                        request.taskId(),
                        userId,
                        TaskSubmissionEntity.SubmissionStatus.COMPLETED
                );

        if (existingCompleted.isPresent()) {
            log.warn("User {} already has completed submission for task {}", userId, request.taskId());
            throw new IllegalStateException("Task already completed");
        }

        TaskSubmissionEntity submission = submissionRepository
                .findByTaskIdAndUserIdAndStatus(
                        request.taskId(),
                        userId,
                        TaskSubmissionEntity.SubmissionStatus.DRAFT
                )
                .orElseGet(() -> TaskSubmissionEntity.builder()
                        .task(task)
                        .userId(userId)
                        .completedReviewsCount(0)
                        .requiredReviewsCount(3)
                        .build());

        submission.setCode(request.code());
        submission.setStatus(TaskSubmissionEntity.SubmissionStatus.COMPLETED);
        submission.setSubmittedAt(LocalDateTime.now());

        TaskSubmissionEntity saved = submissionRepository.save(submission);
        log.info("Solution submitted successfully: submissionId={}", saved.getId());

        return taskSubmissionMapper.mapToTaskSubmissionInfo(saved);
    }

    @Override
    @Transactional
    public TaskSubmissionInfo publishSubmission(Long submissionId, String userId) {
        TaskSubmissionEntity submission = getById(submissionId);

        validateUserOwnsSubmission(submission, userId);

        if (submission.getStatus() != TaskSubmissionEntity.SubmissionStatus.DRAFT) {
            log.warn("Submission {} is not in DRAFT status, current status: {}", submissionId, submission.getStatus());
            throw new IllegalStateException("Only DRAFT submissions can be published");
        }

        submission.setStatus(TaskSubmissionEntity.SubmissionStatus.PUBLISHED);
        submission.setPublishedAt(LocalDateTime.now());

        TaskSubmissionEntity saved = submissionRepository.save(submission);

        return taskSubmissionMapper.mapToTaskSubmissionInfo(saved);
    }

    private void validateUserOwnsSubmission(TaskSubmissionEntity submission, String userId) {
        if (!submission.getUserId().equals(userId)) {
            log.warn("User {} tried to access submission {} owned by {}",
                    userId, submission.getId(), submission.getUserId());
            throw new SecurityException("Not authorized to access this submission");
        }
    }

    private void validateNotSameUser(String authorId, String userId, String message) {
        if (authorId.equals(userId)) {
            log.warn("User {} attempted unauthorized action: {}", userId, message);
            throw new IllegalArgumentException(message);
        }
    }


}
