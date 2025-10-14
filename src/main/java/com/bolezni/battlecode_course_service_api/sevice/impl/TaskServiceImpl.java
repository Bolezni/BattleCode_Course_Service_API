package com.bolezni.battlecode_course_service_api.sevice.impl;

import com.bolezni.battlecode_course_service_api.dto.task.TaskInfo;
import com.bolezni.battlecode_course_service_api.model.TaskEntity;
import com.bolezni.battlecode_course_service_api.repository.TaskRepository;
import com.bolezni.battlecode_course_service_api.sevice.TaskService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TaskServiceImpl implements TaskService {
    TaskRepository taskRepository;

    @Override
    public Set<TaskEntity> getTasksByIds(Set<Long> taskIds) {
        if(taskIds == null || taskIds.isEmpty()){
            log.warn("taskIds is null or empty");
            return Set.of();
        }
        Set<TaskEntity> tasks =  taskRepository.findAllByIdIn(taskIds);

        if (tasks.size() != taskIds.size()) {
            Set<Long> foundIds = tasks.stream()
                    .map(TaskEntity::getId)
                    .collect(Collectors.toSet());

            Set<Long> missingIds = taskIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toSet());

            log.error("Some tasks not found: {}", missingIds);
            throw new IllegalArgumentException("Tasks not found with ids: " + missingIds);
        }

        return tasks;
    }

    @Override
    public List<TaskInfo> getCourseTasks(Long courseId) {
        return List.of();
    }

    @Override
    public TaskInfo getNextTask(Long courseId, Long currentTaskId) {
        return null;
    }
}
