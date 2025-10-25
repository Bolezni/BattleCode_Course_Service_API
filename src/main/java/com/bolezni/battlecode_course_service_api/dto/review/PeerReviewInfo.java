package com.bolezni.battlecode_course_service_api.dto.review;

import java.time.LocalDateTime;
import java.util.Set;

public record PeerReviewInfo(
        Long peerReviewId,
        Long submissionId,
        String reviewerId,
        Integer readability,
        Integer efficiency,
        Integer scalability,
        Integer reliability,
        Integer maintainability,
        String comment,
        String status,
        LocalDateTime reviewedAt,
        Set<ReviewComplaintInfo> complaints
) {
}
