package com.bolezni.battlecode_course_service_api.dto.task;

import com.bolezni.battlecode_course_service_api.dto.course.CourseInfo;

import java.time.LocalDateTime;
import java.util.Set;

public record TaskInfo(
        Long id,
        String title,
        String description,
        String initialCode,
        String testCases,
        String difficulty,
        String type,
        Integer orderIndex,
        Integer estimatedMinutes,
        String content,
        Set<CourseInfo> courses,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
