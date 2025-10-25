package com.bolezni.battlecode_course_service_api.repository;

import com.bolezni.battlecode_course_service_api.model.PeerReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PeerReviewRepository extends JpaRepository<PeerReviewEntity, Long> {
    boolean existsBySubmissionIdAndReviewerId(Long submissionId, String reviewerId);

    List<PeerReviewEntity> findBySubmissionIdAndStatus(Long submissionId, PeerReviewEntity.ReviewStatus status);

    List<PeerReviewEntity> findByReviewerId(String reviewerId);

    List<PeerReviewEntity> findBySubmissionId(Long submissionId);

    @Query("""
        SELECT r FROM PeerReviewEntity r
        JOIN FETCH r.submission s
        WHERE r.reviewerId = :reviewerId
        AND r.status = :status
        ORDER BY r.reviewedAt DESC
        """)
    List<PeerReviewEntity> findByReviewerIdAndStatusWithSubmission(
            @Param("reviewerId") String reviewerId,
            @Param("status") PeerReviewEntity.ReviewStatus status
    );
}
