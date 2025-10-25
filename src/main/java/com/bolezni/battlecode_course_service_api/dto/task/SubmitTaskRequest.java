package com.bolezni.battlecode_course_service_api.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubmitTaskRequest(
        @NotNull(message = "Task ID is required")
        Long taskId,

        @NotBlank(message = "Code is required")
        String code
) {
}
