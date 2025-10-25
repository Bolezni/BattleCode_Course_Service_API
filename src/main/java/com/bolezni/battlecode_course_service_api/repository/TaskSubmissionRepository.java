package com.bolezni.battlecode_course_service_api.repository;

import com.bolezni.battlecode_course_service_api.model.TaskSubmissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskSubmissionRepository extends JpaRepository<TaskSubmissionEntity, Long> {
    boolean existsByTaskIdAndUserIdAndStatus(Long taskId, String userId, TaskSubmissionEntity.SubmissionStatus status);

    @Query("""
        SELECT s FROM TaskSubmissionEntity s
        WHERE s.task.id = :taskId
        AND s.userId != :reviewerId
        AND s.status IN :statuses
        AND s.completedReviewsCount < s.requiredReviewsCount
        AND NOT EXISTS (
            SELECT 1 FROM PeerReviewEntity r
            WHERE r.submission = s
            AND r.reviewerId = :reviewerId
        )
        ORDER BY s.publishedAt ASC
        """)
    List<TaskSubmissionEntity> findAvailableForReview(
            @Param("taskId") Long taskId,
            @Param("reviewerId") String reviewerId,
            @Param("statuses") List<TaskSubmissionEntity.SubmissionStatus> statuses
    );

    List<TaskSubmissionEntity> findByUserId(String userId);

    Optional<TaskSubmissionEntity> findByTaskIdAndUserIdAndStatus(Long taskId, String userId, TaskSubmissionEntity.SubmissionStatus status);
}
