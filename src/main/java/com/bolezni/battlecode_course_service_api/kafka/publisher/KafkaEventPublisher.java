package com.bolezni.battlecode_course_service_api.kafka.publisher;

import com.bolezni.battlecode_course_service_api.kafka.event.SubscriptionEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class KafkaEventPublisher {

    KafkaTemplate<String, SubscriptionEvent> kafkaTemplate;
    private static final String SUBSCRIPTION_TOPIC = "subscription-events";

    public void sendSubscriptionEvent(SubscriptionEvent event) {
        try {
            CompletableFuture<SendResult<String, SubscriptionEvent>> future =
                    kafkaTemplate.send(SUBSCRIPTION_TOPIC, event.getUserId(), event);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Subscription event sent successfully - Type: {}, User: {}, Course: {}, Offset: {}",
                            event.getEventType(), event.getUserId(), event.getCourseId(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to send subscription event - Type: {}, User: {}, Course: {}",
                            event.getEventType(), event.getUserId(), event.getCourseId(), ex);
                }
            });

        } catch (Exception e) {
            log.error("Error sending subscription event to Kafka - Type: {}, User: {}, Course: {}",
                    event.getEventType(), event.getUserId(), event.getCourseId(), e);
        }
    }

    public void sendSubscriptionCreated(String userId, Long courseId) {
        SubscriptionEvent event = SubscriptionEvent.createSubscriptionEvent(userId, courseId);
        sendSubscriptionEvent(event);
    }

    public void sendSubscriptionCancelled(String userId, Long courseId) {
        SubscriptionEvent event = SubscriptionEvent.cancelSubscriptionEvent(userId, courseId);
        sendSubscriptionEvent(event);
    }

    public void sendSubscriptionUpdated(String userId, Long courseId) {
        SubscriptionEvent event = SubscriptionEvent.updatelSubscriptionEvent(userId, courseId);
        sendSubscriptionEvent(event);
    }
}
