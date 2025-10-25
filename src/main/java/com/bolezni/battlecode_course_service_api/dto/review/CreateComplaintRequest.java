package com.bolezni.battlecode_course_service_api.dto.review;

import com.bolezni.battlecode_course_service_api.model.ReviewComplaintEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateComplaintRequest(
        @NotNull ReviewComplaintEntity.ComplaintReason reason,
        @NotBlank String description
) {
}
