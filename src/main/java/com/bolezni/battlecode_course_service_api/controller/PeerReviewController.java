package com.bolezni.battlecode_course_service_api.controller;

import com.bolezni.battlecode_course_service_api.dto.UserInfo;
import com.bolezni.battlecode_course_service_api.dto.review.*;
import com.bolezni.battlecode_course_service_api.security.JwtAuth;
import com.bolezni.battlecode_course_service_api.sevice.PeerReviewService;
import com.bolezni.battlecode_course_service_api.sevice.TaskSubmissionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "peer_reviews_methods")
@JwtAuth
@RestController
@RequestMapping("/api/peer-reviews")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PeerReviewController {

    PeerReviewService reviewService;
    TaskSubmissionService taskSubmissionService;

    /**
     * Проверка возможности рецензирования
     */
    @GetMapping("/submissions/{submissionId}/can-review")
    public ResponseEntity<CanReviewResponse> canReview(
            @PathVariable Long submissionId, HttpServletRequest request) {

        UserInfo userInfo = (UserInfo) request.getAttribute("userInfo");
        boolean canReview = taskSubmissionService.canReview(submissionId, userInfo.userId());
        return ResponseEntity.ok(new CanReviewResponse(canReview));
    }

    /**
     * Создание отзыва
     */
    @PostMapping("/submissions/{submissionId}/reviews")
    public ResponseEntity<PeerReviewInfo> createReview(
            @PathVariable Long submissionId,
            @Valid @RequestBody CreateReviewRequest request, HttpServletRequest httpRequest) {

        UserInfo userInfo = (UserInfo) httpRequest.getAttribute("userInfo");
        PeerReviewInfo review = reviewService.createReview(submissionId, userInfo.userId(), request);
        return ResponseEntity.ok(review);
    }

    /**
     * Получение всех отзывов для решения
     */
    @GetMapping("/submissions/{submissionId}/reviews")
    public ResponseEntity<List<PeerReviewInfo>> getReviews(
            @PathVariable Long submissionId) {

        List<PeerReviewInfo> reviews = reviewService.getReviewsBySubmission(submissionId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Подача жалобы на отзыв
     */
    @PostMapping("/reviews/{reviewId}/complaints")
    public ResponseEntity<ReviewComplaintInfo> createComplaint(
            @PathVariable Long reviewId,
            @RequestBody CreateComplaintRequest request, HttpServletRequest httpRequest) {

        UserInfo userInfo = (UserInfo) httpRequest.getAttribute("userInfo");
        ReviewComplaintInfo complaint = reviewService.createComplaint(reviewId, userInfo.userId(), request);
        return ResponseEntity.ok(complaint);
    }

    /**
     * Получение всех жалоб пользователя
     */
    @GetMapping("/complaints/my")
    public ResponseEntity<List<ReviewComplaintInfo>> getMyComplaints(HttpServletRequest httpRequest) {

        UserInfo userInfo = (UserInfo) httpRequest.getAttribute("userInfo");
        List<ReviewComplaintInfo> complaints = reviewService.getComplaintsByUser(userInfo.userId());
        return ResponseEntity.ok(complaints);
    }

    /**
     * Получение отзывов, оставленных пользователем
     */
    @GetMapping("/reviews/my")
    public ResponseEntity<List<PeerReviewInfo>> getMyReviews(HttpServletRequest httpRequest) {

        UserInfo userInfo = (UserInfo) httpRequest.getAttribute("userInfo");
        List<PeerReviewInfo> reviews = reviewService.getReviewsByReviewer(userInfo.userId());
        return ResponseEntity.ok(reviews);
    }
}
