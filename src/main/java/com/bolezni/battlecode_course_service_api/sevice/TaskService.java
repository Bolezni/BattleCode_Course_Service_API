package com.bolezni.battlecode_course_service_api.sevice;

import com.bolezni.battlecode_course_service_api.dto.task.TaskInfo;
import com.bolezni.battlecode_course_service_api.model.TaskEntity;

import java.util.List;
import java.util.Set;

public interface TaskService {
    Set<TaskEntity> getTasksByIds(Set<Long> taskIds);

    List<TaskInfo> getCourseTasks(Long courseId);

    TaskInfo getNextTask(Long courseId, Long currentTaskId);


}

