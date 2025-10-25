package com.bolezni.battlecode_course_service_api.sevice;

import com.bolezni.battlecode_course_service_api.dto.review.CreateComplaintRequest;
import com.bolezni.battlecode_course_service_api.dto.review.CreateReviewRequest;
import com.bolezni.battlecode_course_service_api.dto.review.PeerReviewInfo;
import com.bolezni.battlecode_course_service_api.dto.review.ReviewComplaintInfo;

import java.util.List;

public interface PeerReviewService {

    PeerReviewInfo createReview(Long submissionId, String reviewerId,
                                CreateReviewRequest request);

    ReviewComplaintInfo createComplaint(Long reviewId, String complainantId,
                                        CreateComplaintRequest request);


    List<ReviewComplaintInfo> getComplaintsByUser(String userId);

    List<PeerReviewInfo> getReviewsByReviewer(String userId);


    List<PeerReviewInfo> getReviewsBySubmission(Long submissionId);

}
