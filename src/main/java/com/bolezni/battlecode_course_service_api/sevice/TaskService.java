package com.bolezni.battlecode_course_service_api.sevice;

import com.bolezni.battlecode_course_service_api.dto.task.TaskInfo;
import com.bolezni.battlecode_course_service_api.dto.task.TaskUpdateDto;
import com.bolezni.battlecode_course_service_api.model.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface TaskService {
    Set<TaskEntity> getTasksByIds(Set<Long> taskIds);

    Set<TaskInfo> getTasksInfoByIds(Set<Long> taskIds);

    Set<TaskInfo> getCourseTasks(Long courseId);

    Page<TaskInfo> getCourseTasks(Long courseId, Pageable pageable);

    TaskInfo getNextTask(Long courseId, Long currentTaskId);

    TaskEntity save(TaskEntity task);

    TaskInfo updateTask(Long taskId, TaskUpdateDto updatedTask);

    void deleteTask(Long taskId);

    void saveAll(Set<TaskEntity> tasks);

    Set<TaskEntity> getTasksByIdsWithCourses(Set<Long> ids);
}

