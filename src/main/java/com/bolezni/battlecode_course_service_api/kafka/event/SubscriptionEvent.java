package com.bolezni.battlecode_course_service_api.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionEvent {
    private EventType eventType;
    private String userId;
    private Long courseId;
    private LocalDateTime timestamp;

    public static SubscriptionEvent createSubscriptionEvent(String userId, Long courseId) {
        return new SubscriptionEvent(
                EventType.SUBSCRIPTION_CREATED,
                userId,
                courseId,
                LocalDateTime.now()
        );
    }

    public static SubscriptionEvent cancelSubscriptionEvent(String userId, Long courseId) {
        return new SubscriptionEvent(
                EventType.SUBSCRIPTION_CANCELLED,
                userId,
                courseId,
                LocalDateTime.now()
        );
    }

    public static SubscriptionEvent updatelSubscriptionEvent(String userId, Long courseId) {
        return new SubscriptionEvent(
                EventType.SUBSCRIPTION_UPDATED,
                userId,
                courseId,
                LocalDateTime.now()
        );
    }
}
