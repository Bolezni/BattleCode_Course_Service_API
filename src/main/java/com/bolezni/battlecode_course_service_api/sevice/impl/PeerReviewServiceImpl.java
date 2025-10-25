package com.bolezni.battlecode_course_service_api.sevice.impl;

import com.bolezni.battlecode_course_service_api.dto.review.CreateComplaintRequest;
import com.bolezni.battlecode_course_service_api.dto.review.CreateReviewRequest;
import com.bolezni.battlecode_course_service_api.dto.review.PeerReviewInfo;
import com.bolezni.battlecode_course_service_api.dto.review.ReviewComplaintInfo;
import com.bolezni.battlecode_course_service_api.mapper.PeerReviewMapper;
import com.bolezni.battlecode_course_service_api.mapper.ReviewComplaintMapper;
import com.bolezni.battlecode_course_service_api.model.PeerReviewEntity;
import com.bolezni.battlecode_course_service_api.model.ReviewComplaintEntity;
import com.bolezni.battlecode_course_service_api.model.TaskSubmissionEntity;
import com.bolezni.battlecode_course_service_api.repository.PeerReviewRepository;
import com.bolezni.battlecode_course_service_api.repository.ReviewComplaintRepository;
import com.bolezni.battlecode_course_service_api.repository.TaskSubmissionRepository;
import com.bolezni.battlecode_course_service_api.sevice.PeerReviewService;
import com.bolezni.battlecode_course_service_api.sevice.TaskSubmissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PeerReviewServiceImpl implements PeerReviewService {
    TaskSubmissionRepository submissionRepository;
    PeerReviewRepository reviewRepository;
    ReviewComplaintRepository reviewComplaintRepository;
    TaskSubmissionService taskSubmissionService;

    ReviewComplaintMapper reviewComplaintMapper;
    PeerReviewMapper peerReviewMapper;

    @Override
    @Transactional
    public PeerReviewInfo createReview(Long submissionId, String reviewerId, CreateReviewRequest request) {
        if (!taskSubmissionService.canReview(submissionId, reviewerId)) {
            log.warn("Not authorized to review this submission");
            throw new RuntimeException("Not authorized to review this submission");
        }

        TaskSubmissionEntity submission = getSubmissionById(submissionId);

        validateRating(request.readability());
        validateRating(request.efficiency());
        validateRating(request.scalability());
        validateRating(request.readability());
        validateRating(request.maintainability());

        PeerReviewEntity review = PeerReviewEntity.builder()
                .submission(submission)
                .reviewerId(reviewerId)
                .readability(request.readability())
                .efficiency(request.efficiency())
                .scalability(request.scalability())
                .reliability(request.readability())
                .maintainability(request.maintainability())
                .comment(request.comment())
                .status(PeerReviewEntity.ReviewStatus.ACTIVE)
                .reviewedAt(LocalDateTime.now())
                .build();

        review = reviewRepository.save(review);

        submission.setCompletedReviewsCount(submission.getCompletedReviewsCount() + 1);

        updateSubmissionAverages(submission);

        if (submission.getCompletedReviewsCount() >= submission.getRequiredReviewsCount()) {
            submission.setStatus(TaskSubmissionEntity.SubmissionStatus.REVIEWED);
        } else if (submission.getStatus() == TaskSubmissionEntity.SubmissionStatus.PUBLISHED) {
            submission.setStatus(TaskSubmissionEntity.SubmissionStatus.UNDER_REVIEW);
        }

        submissionRepository.save(submission);

        return peerReviewMapper.mapToPeerReviewInfo(review);
    }

    @Override
    @Transactional
    public ReviewComplaintInfo createComplaint(Long reviewId, String complainantId, CreateComplaintRequest request) {
        PeerReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!isCorrectUser(review.getSubmission().getUserId(), complainantId)) {
            log.warn("Only submission author can complain");
            throw new RuntimeException("Only submission author can complain");
        }

        ReviewComplaintEntity complaint = ReviewComplaintEntity.builder()
                .review(review)
                .complainantId(complainantId)
                .reason(request.reason())
                .description(request.description())
                .status(ReviewComplaintEntity.ComplaintStatus.PENDING)
                .build();

        complaint = reviewComplaintRepository.save(complaint);

        review.setStatus(PeerReviewEntity.ReviewStatus.FLAGGED);
        reviewRepository.save(review);

        return reviewComplaintMapper.mapToReviewComplaintInfo(complaint);
    }

    @Override
    public List<ReviewComplaintInfo> getComplaintsByUser(String userId) {
        return reviewComplaintRepository.findByComplainantId(userId)
                .stream()
                .map(reviewComplaintMapper::mapToReviewComplaintInfo)
                .toList();
    }

    @Override
    public List<PeerReviewInfo> getReviewsByReviewer(String userId) {
        return reviewRepository.findByReviewerId(userId).stream()
                .map(peerReviewMapper::mapToPeerReviewInfo)
                .toList();
    }

    @Override
    public List<PeerReviewInfo> getReviewsBySubmission(Long submissionId) {
        return reviewRepository.findBySubmissionId(submissionId).stream()
                .map(peerReviewMapper::mapToPeerReviewInfo)
                .toList();
    }

    private void updateSubmissionAverages(TaskSubmissionEntity submission) {
        List<PeerReviewEntity> activeReviews = reviewRepository
                .findBySubmissionIdAndStatus(submission.getId(), PeerReviewEntity.ReviewStatus.ACTIVE);

        if (activeReviews.isEmpty()) {
            return;
        }

        double avgReadability = activeReviews.stream()
                .mapToInt(PeerReviewEntity::getReadability)
                .average()
                .orElse(0.0);

        double avgEfficiency = activeReviews.stream()
                .mapToInt(PeerReviewEntity::getEfficiency)
                .average()
                .orElse(0.0);

        double avgScalability = activeReviews.stream()
                .mapToInt(PeerReviewEntity::getScalability)
                .average()
                .orElse(0.0);

        double avgReliability = activeReviews.stream()
                .mapToInt(PeerReviewEntity::getReliability)
                .average()
                .orElse(0.0);

        double avgMaintainability = activeReviews.stream()
                .mapToInt(PeerReviewEntity::getMaintainability)
                .average()
                .orElse(0.0);

        submission.setAvgReadability(avgReadability);
        submission.setAvgEfficiency(avgEfficiency);
        submission.setAvgScalability(avgScalability);
        submission.setAvgReliability(avgReliability);
        submission.setAvgMaintainability(avgMaintainability);

        double overallScore = (avgReadability + avgEfficiency + avgScalability +
                avgReliability + avgMaintainability) / 5.0;
        submission.setOverallScore(overallScore);
    }

    private void validateRating(Integer rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
    }

    private boolean isCorrectUser(String userFromSubmission, String userIdFromRequest) {
        return userFromSubmission.equals(userIdFromRequest);
    }

    private TaskSubmissionEntity getSubmissionById(Long submissionId) {
        return submissionRepository.findById(submissionId)
                .orElseThrow(() -> {
                    log.warn("Submission not found");
                    return new RuntimeException("Submission not found");
                });
    }
}
