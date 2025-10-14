package com.bolezni.battlecode_course_service_api.dto.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.HashSet;
import java.util.Set;

public record CourseUpdate(
        @NotBlank(message = "Title cannot be blank")
        String title,

        @NotBlank(message = "Description cannot be blank")
        String description,

        @NotBlank(message = "Image URL cannot be blank")
        String imageUrl,

        @NotBlank(message = "Level cannot be blank")
        String level,

        @NotNull(message = "Price cannot be null")
        @PositiveOrZero(message = "Price must be positive or zero")
        Double price,

        @NotNull(message = "isPublished cannot be null")
        Boolean isPublished,

        @NotNull(message = "taskIdsToAdd cannot be null")
        Set<Long> taskIdsToAdd,

        @NotNull(message = "taskIdsToRemove cannot be null")
        Set<Long> taskIdsToRemove
) {
    public CourseUpdate {
        if (taskIdsToAdd == null) taskIdsToAdd = new HashSet<>();
        if (taskIdsToRemove == null) taskIdsToRemove = new HashSet<>();

        Set<Long> intersection = new HashSet<>(taskIdsToAdd);
        intersection.retainAll(taskIdsToRemove);
        if (!intersection.isEmpty()) {
            throw new IllegalArgumentException("Cannot add and remove same tasks: " + intersection);
        }
    }
}
