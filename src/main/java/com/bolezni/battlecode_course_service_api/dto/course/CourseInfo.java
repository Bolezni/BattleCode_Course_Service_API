package com.bolezni.battlecode_course_service_api.dto.course;

import com.bolezni.battlecode_course_service_api.dto.task.TaskInfo;
import com.bolezni.battlecode_course_service_api.model.SubscriptionEntity;

import java.time.LocalDateTime;
import java.util.Set;

public record CourseInfo(
        Long id,
        String title,
        String description,
        String imageUrl,
        String level,
        Double price,
        Boolean isPublished,
        Long authorId,
        Set<TaskInfo> tasks,
        Set<SubscriptionEntity> subscriptions,
        Integer studentCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
