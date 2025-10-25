package com.bolezni.battlecode_course_service_api.repository;

import com.bolezni.battlecode_course_service_api.model.ReviewComplaintEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewComplaintRepository extends JpaRepository<ReviewComplaintEntity, Long> {
    List<ReviewComplaintEntity> findByStatus(ReviewComplaintEntity.ComplaintStatus status);

    List<ReviewComplaintEntity> findByComplainantId(String complainantId);

    List<ReviewComplaintEntity> findByReviewId(Long reviewId);

    boolean existsByReviewIdAndComplainantId(Long reviewId, String complainantId);

}
