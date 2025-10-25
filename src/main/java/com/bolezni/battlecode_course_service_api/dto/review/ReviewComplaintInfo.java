package com.bolezni.battlecode_course_service_api.dto.review;

import java.time.LocalDateTime;

public record ReviewComplaintInfo(
        Long reviewComplainId,
        Long reviewId,
        String complainantId,
        String reason,
        String description,
        String status,
        LocalDateTime resolvedAt,
        String resolvedBy,
        String resolutionNote
) {
}
