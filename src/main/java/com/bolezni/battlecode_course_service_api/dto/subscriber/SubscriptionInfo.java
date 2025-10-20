package com.bolezni.battlecode_course_service_api.dto.subscriber;

import java.time.LocalDateTime;

public record SubscriptionInfo(
        Long subscriptionId,
        String userId,
        Long courseInfo,
        LocalDateTime subscribedAt,
        LocalDateTime completedAt,
        Double progress,
        Boolean isActive
) {
}
