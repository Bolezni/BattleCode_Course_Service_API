package com.bolezni.battlecode_course_service_api.sevice.impl;

import com.bolezni.battlecode_course_service_api.dto.task.TaskInfo;
import com.bolezni.battlecode_course_service_api.dto.task.TaskUpdateDto;
import com.bolezni.battlecode_course_service_api.mapper.TaskMapper;
import com.bolezni.battlecode_course_service_api.model.TaskEntity;
import com.bolezni.battlecode_course_service_api.repository.TaskRepository;
import com.bolezni.battlecode_course_service_api.sevice.TaskService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TaskServiceImpl implements TaskService {
    TaskRepository taskRepository;
    TaskMapper taskMapper;

    @Override
    @Transactional
    public void saveAll(Set<TaskEntity> tasks) {
        taskRepository.saveAll(tasks);
    }

    @Override
    public Set<TaskEntity> getTasksByIdsWithCourses(Set<Long> ids) {
        return taskRepository.findByIdInWithCourses(ids);
    }

    @Override
    public Set<TaskEntity> getTasksByIds(Set<Long> taskIds) {
        if (taskIds == null || taskIds.isEmpty()) {
            log.warn("taskIds is null or empty");
            return Set.of();
        }
        Set<TaskEntity> tasks = taskRepository.findAllByIdIn(taskIds);

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
    public Set<TaskInfo> getTasksInfoByIds(Set<Long> taskIds) {

        Set<TaskEntity> tasks = getTasksByIds(taskIds);
        return tasks.stream().map(taskMapper::mapToTaskInfo).collect(Collectors.toSet());
    }

    @Override
    public Page<TaskInfo> getCourseTasks(Long courseId, Pageable pageable) {
        if (courseId == null) {
            throw new IllegalArgumentException("course_id is required");
        }
        Page<TaskEntity> tasks = taskRepository.findAllByCourseId(courseId, pageable);
        return tasks.map(taskMapper::mapToTaskInfo);
    }

    @Override
    public Set<TaskInfo> getCourseTasks(Long courseId) {
        if (courseId == null) {
            throw new IllegalArgumentException("course_id is required");
        }
        Set<TaskEntity> tasks = taskRepository.findAllByCourseId(courseId);
        return tasks.stream().map(taskMapper::mapToTaskInfo).collect(Collectors.toSet());
    }

    @Override
    public TaskInfo getNextTask(Long courseId, Long currentTaskId) {
        return null;
    }

    @Override
    @Transactional
    public TaskEntity save(TaskEntity task) {
        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public TaskInfo updateTask(Long taskId, TaskUpdateDto updatedTask) {
        if (updatedTask == null || taskId == null) {
            log.error("UpdateDto or id is null");
            throw new IllegalArgumentException("UpdateDto or id is null");
        }
        TaskEntity currentTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        taskMapper.updateTaskEntity(updatedTask, currentTask);
        TaskEntity saved = taskRepository.save(currentTask);
        log.info("Task updated successfully with id: {}", taskId);
        return taskMapper.mapToTaskInfo(saved);
    }

    @Override
    @Transactional
    public void deleteTask(Long taskId) {
        if (taskId == null) {
            log.error("Attempted to delete task with null taskId");
            throw new IllegalArgumentException("taskId cannot be null");
        }

        if (!taskRepository.existsById(taskId)) {
            log.error("Task not found with id: {}", taskId);
            throw new RuntimeException("Task not found with id: " + taskId);
        }

        taskRepository.deleteById(taskId);
        log.info("Task deleted successfully with id: {}", taskId);
    }
}
