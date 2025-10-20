package com.bolezni.battlecode_course_service_api.dto.task;

import jakarta.validation.constraints.NotNull;

public record TaskUpdateDto(
        @NotNull String title,
        @NotNull String description,
        @NotNull String initialCode,
        @NotNull String testCases,
        @NotNull String difficulty,
        @NotNull String type,
        @NotNull Integer orderIndex,
        @NotNull Integer estimatedMinutes,
        @NotNull String content
) {
}
