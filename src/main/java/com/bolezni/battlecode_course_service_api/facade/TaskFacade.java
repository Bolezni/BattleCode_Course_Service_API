package com.bolezni.battlecode_course_service_api.facade;

import com.bolezni.battlecode_course_service_api.dto.task.TaskCreateDto;
import com.bolezni.battlecode_course_service_api.dto.task.TaskInfo;
import com.bolezni.battlecode_course_service_api.mapper.TaskMapper;
import com.bolezni.battlecode_course_service_api.model.TaskEntity;
import com.bolezni.battlecode_course_service_api.sevice.CourseService;
import com.bolezni.battlecode_course_service_api.sevice.TaskService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TaskFacade {
    TaskService taskService;
    CourseService courseService;
    TaskMapper taskMapper;

    public TaskInfo createNewTask(TaskCreateDto taskCreateDto) {
        if (taskCreateDto == null) {
            throw new IllegalArgumentException("taskCreateDto is null");
        }

        TaskEntity task = taskMapper.mapToTaskEntity(taskCreateDto);

        TaskEntity saved = taskService.save(task);

        if (taskCreateDto.courseIds() != null && !taskCreateDto.courseIds().isEmpty()) {
            courseService.addTaskToCourses(saved, taskCreateDto.courseIds());
        }

        return taskMapper.mapToTaskInfo(saved);
    }
}
