package com.bolezni.battlecode_course_service_api.dto.course;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.Set;

public record CourseCreateDto(
        @NotBlank String title,
        @NotBlank String description,
        @NotBlank String imageUrl,
        @NotBlank String level,
        @NotNull @PositiveOrZero Double price,
        @NotNull Set<Long> taskIds
) {

}
