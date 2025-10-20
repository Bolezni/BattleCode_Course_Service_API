package com.bolezni.battlecode_course_service_api.sevice.impl;

import com.bolezni.battlecode_course_service_api.dto.subscriber.SubscriptionInfo;
import com.bolezni.battlecode_course_service_api.kafka.publisher.KafkaEventPublisher;
import com.bolezni.battlecode_course_service_api.mapper.SubscriptionMapper;
import com.bolezni.battlecode_course_service_api.model.CourseEntity;
import com.bolezni.battlecode_course_service_api.model.SubscriptionEntity;
import com.bolezni.battlecode_course_service_api.repository.SubscriptionRepository;
import com.bolezni.battlecode_course_service_api.sevice.CourseService;
import com.bolezni.battlecode_course_service_api.sevice.SubscriptionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SubscriptionServiceImpl implements SubscriptionService {
    SubscriptionRepository subscriptionRepository;
    CourseService courseService;
    KafkaEventPublisher kafkaEventPublisher;
    SubscriptionMapper subscriptionMapper;

    @Override
    public SubscriptionInfo subscribeUserToCourse(String userId, Long courseId) {
        validateId(userId);
        validateId(courseId);

        CourseEntity course = courseService.getCourseEntity(courseId);

        if (subscriptionRepository.existsByUserIdAndCourseIdAndIsActiveTrue(userId, courseId)) {
            throw new RuntimeException("Subscription already exists");
        }

        Optional<SubscriptionEntity> existingSubscription = subscriptionRepository
                .findByUserIdAndCourseId(userId, courseId);

        SubscriptionEntity subscription;

        if (existingSubscription.isPresent()) {
            subscription = existingSubscription.get();
            subscription.setIsActive(true);
            subscription.setSubscribedAt(LocalDateTime.now());
            log.info("Reactivating existing subscription - User: {}, Course: {}", userId, courseId);
        } else {
            subscription = SubscriptionEntity.builder()
                    .userId(userId)
                    .course(course)
                    .subscribedAt(LocalDateTime.now())
                    .isActive(true)
                    .build();
            log.info("Creating new subscription - User: {}, Course: {}", userId, courseId);
        }

        SubscriptionEntity savedSubscription = subscriptionRepository.save(subscription);

        try {
            kafkaEventPublisher.sendSubscriptionCreated(savedSubscription.getUserId(), savedSubscription.getCourse().getId());
            log.info("Subscription created and event sent - User: {}, Course: {}, Subscription ID: {}",
                    userId, courseId, savedSubscription.getId());
        } catch (Exception e) {
            log.error("Failed to send Kafka event after subscription creation - User: {}, Course: {}",
                    userId, courseId, e);
        }


        return subscriptionMapper.toSubscriber(savedSubscription);
    }

    @Override
    public void unsubscribeUserToCourse(String userId, Long courseId) {
        validateId(userId);
        validateId(courseId);

        SubscriptionEntity subscription = subscriptionRepository
                .findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        subscription.setIsActive(false);
        SubscriptionEntity saved = subscriptionRepository.save(subscription);

        kafkaEventPublisher.sendSubscriptionCancelled(saved.getUserId(), saved.getCourse().getId());

        log.info("Subscription cancelled - User: {}, Course: {}", userId, courseId);
    }

    @Override
    public void updateSubscriptionProgress(String userId, Long courseId, Double progress) {
        validateId(userId);
        validateId(courseId);

        SubscriptionEntity subscription = subscriptionRepository
                .findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        subscription.setProgress(progress);
        SubscriptionEntity saved = subscriptionRepository.save(subscription);


        kafkaEventPublisher.sendSubscriptionUpdated(saved.getUserId(), saved.getCourse().getId());

        log.debug("Subscription progress updated - User: {}, Course: {}, Progress: {}",
                userId, courseId, progress);
    }

    private <T> void validateId(T id) {
        switch (id) {
            case null -> {
                log.error("ID is null");
                throw new IllegalArgumentException("ID is null");
            }
            case String s when s.isBlank() -> {
                log.error("String ID is empty");
                throw new IllegalArgumentException("String ID is empty");
            }
            case Number number when number.longValue() <= 0 -> {
                log.error("Numeric ID must be positive");
                throw new IllegalArgumentException("Numeric ID must be positive");
            }
            case UUID uuid when uuid.toString().isBlank() -> {
                log.error("UUID is invalid");
                throw new IllegalArgumentException("UUID is invalid");
            }
            default -> {
            }
        }

    }
}
