package com.bolezni.battlecode_course_service_api.dto.course;

import com.bolezni.battlecode_course_service_api.dto.subscriber.SubscriptionInfo;
import com.bolezni.battlecode_course_service_api.dto.task.TaskInfo;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record CourseInfo(
        Long id,
        String title,
        String description,
        String imageUrl,
        String level,
        Double price,
        Boolean isPublished,
        String authorId,
        Set<TaskInfo> tasks,
        Set<SubscriptionInfo> subscriptions,
        Integer studentCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
