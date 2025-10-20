package com.bolezni.battlecode_course_service_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddTasksRequest {
    private Set<Long> taskIds;
}