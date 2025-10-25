package com.bolezni.battlecode_course_service_api.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionEvent {
    private EventType eventType;
    private String userId;
    private Long courseId;
    private Double progress;
    private LocalDateTime timestamp;

    public static SubscriptionEvent createSubscriptionEvent(String userId, Long courseId) {
        return SubscriptionEvent.builder()
                .eventType(EventType.SUBSCRIPTION_CREATED)
                .userId(userId)
                .courseId(courseId)
                .build();
    }

    public static SubscriptionEvent cancelSubscriptionEvent(String userId, Long courseId) {
        return SubscriptionEvent.builder()
                .eventType(EventType.SUBSCRIPTION_CANCELLED)
                .userId(userId)
                .courseId(courseId)
                .build();
    }

    public static SubscriptionEvent updatelSubscriptionEvent(String userId, Long courseId, Double progress) {
        return SubscriptionEvent.builder()
                .eventType(EventType.SUBSCRIPTION_UPDATED)
                .userId(userId)
                .courseId(courseId)
                .progress(progress)
                .build();
    }
}
