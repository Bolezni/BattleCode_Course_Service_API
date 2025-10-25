package com.bolezni.battlecode_course_service_api.dto.review;

import jakarta.validation.constraints.*;

public record CreateReviewRequest(
        @NotNull @Positive @Min(1) @Max(5) Integer readability,
        @NotNull @Positive @Min(1) @Max(5) Integer efficiency,
        @NotNull @Positive @Min(1) @Max(5) Integer scalability,
        @NotNull @Positive @Min(1) @Max(5) Integer reliability,
        @NotNull @Positive @Min(1) @Max(5) Integer maintainability,
        @NotBlank String comment
) {
}
