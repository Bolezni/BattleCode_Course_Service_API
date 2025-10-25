package com.bolezni.battlecode_course_service_api.sevice;

import com.bolezni.battlecode_course_service_api.dto.task.SaveDraftRequest;
import com.bolezni.battlecode_course_service_api.dto.task.SubmitTaskRequest;
import com.bolezni.battlecode_course_service_api.dto.task.TaskSubmissionInfo;
import com.bolezni.battlecode_course_service_api.model.TaskEntity;
import com.bolezni.battlecode_course_service_api.model.TaskSubmissionEntity;

import java.util.List;

public interface TaskSubmissionService {
    TaskSubmissionEntity getById(Long id);

    TaskSubmissionEntity save(TaskSubmissionEntity submission);

    boolean canReview(Long submissionId, String reviewerId);

    List<TaskSubmissionInfo> getSubmissionsForReview(String reviewerId, Long taskId);

    List<TaskSubmissionInfo> getSubmissionsByUser(String userId);

    TaskSubmissionEntity getOrCreateSubmission(TaskEntity task, String reviewerId, TaskSubmissionEntity.SubmissionStatus status, SaveDraftRequest request);

    TaskSubmissionInfo getSubmission(String userId, Long submissionId);

    TaskSubmissionInfo submitSolution(String userId, SubmitTaskRequest request);

    TaskSubmissionInfo publishSubmission(Long submissionId, String userId);
}
