package com.bolezni.battlecode_course_service_api.dto.task;

import com.bolezni.battlecode_course_service_api.dto.review.PeerReviewInfo;

import java.time.LocalDateTime;
import java.util.Set;

public record TaskSubmissionInfo(
        Long taskSubmissionId,
        Long taskId,
        String userId,
        String code,
        String status,
        LocalDateTime submittedAt,
        LocalDateTime publishedAt,
        Integer completedReviewsCount,
        Integer requiredReviewsCount,
        Double avgReadability,
        Double avgEfficiency,
        Double avgScalability,
        Double avgReliability,
        Double avgMaintainability,
        Double overallScore,
        Set<PeerReviewInfo> reviews
) {
}
