package com.bolezni.battlecode_course_service_api.sevice;

import com.bolezni.battlecode_course_service_api.dto.subscriber.SubscriptionInfo;

public interface SubscriptionService {
    SubscriptionInfo subscribeUserToCourse(String userId, Long courseId);
    void unsubscribeUserToCourse(String userId, Long courseId);
    void updateSubscriptionProgress(String userId, Long courseId, Double progress);
}
