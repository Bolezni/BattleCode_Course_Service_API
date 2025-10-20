package com.bolezni.battlecode_course_service_api.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record TaskCreateDto(
        @NotBlank String title,
        @NotBlank String description,
        @NotBlank String initialCode,
        @NotBlank String testCases,
        @NotBlank String difficulty,
        @NotBlank String type,
        @NotNull Integer orderIndex,
        @NotNull  Integer estimatedMinutes,
        @NotBlank String content,
        @NotNull Set<Long> courseIds
) {
}
