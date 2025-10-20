package com.bolezni.battlecode_course_service_api.controller;

import com.bolezni.battlecode_course_service_api.dto.UserInfo;
import com.bolezni.battlecode_course_service_api.dto.subscriber.SubscriptionInfo;
import com.bolezni.battlecode_course_service_api.security.JwtAuth;
import com.bolezni.battlecode_course_service_api.sevice.SubscriptionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "subscription_methods")
@JwtAuth
@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/subscribe")
    public ResponseEntity<SubscriptionInfo> subscribeUserToCourse(@RequestParam(name = "course_id") Long courseId,
                                                                  HttpServletRequest httpRequest) {
        UserInfo userInfo = (UserInfo) httpRequest.getAttribute("userInfo");
        SubscriptionInfo subscription = subscriptionService.subscribeUserToCourse(userInfo.userId(), courseId);
        return ResponseEntity.ok(subscription);
    }

    @PostMapping("/cancel")
    public ResponseEntity<Void> cancelSubscription(
            @RequestParam(name = "course_id") Long courseId, HttpServletRequest httpRequest) {
        UserInfo userInfo = (UserInfo) httpRequest.getAttribute("userInfo");
        subscriptionService.unsubscribeUserToCourse(userInfo.userId(), courseId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/progress")
    public ResponseEntity<Void> updateProgress(
            @RequestParam(name = "course_id") Long courseId,
            @RequestParam(name = "progress") Double progress,
            HttpServletRequest httpRequest) {
        UserInfo userInfo = (UserInfo) httpRequest.getAttribute("userInfo");
        subscriptionService.updateSubscriptionProgress(userInfo.userId(), courseId, progress);
        return ResponseEntity.ok().build();
    }
}