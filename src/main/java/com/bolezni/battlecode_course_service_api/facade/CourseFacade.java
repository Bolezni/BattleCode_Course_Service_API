package com.bolezni.battlecode_course_service_api.facade;

import com.bolezni.battlecode_course_service_api.dto.course.CourseCreateDto;
import com.bolezni.battlecode_course_service_api.dto.course.CourseInfo;
import com.bolezni.battlecode_course_service_api.mapper.CourseMapper;
import com.bolezni.battlecode_course_service_api.model.CourseEntity;
import com.bolezni.battlecode_course_service_api.model.TaskEntity;
import com.bolezni.battlecode_course_service_api.sevice.CourseService;
import com.bolezni.battlecode_course_service_api.sevice.TaskService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CourseFacade {
    CourseService courseService;
    TaskService taskService;
    CourseMapper courseMapper;

    public void addTasksToCourse(Long courseId, Set<Long> taskIds) {
        if (courseId == null) {
            log.error("courseId is null");
            throw new IllegalArgumentException("courseId is null");
        }

        CourseEntity currentCourse = courseService.getCourseEntityWithTasks(courseId);

        if (taskIds == null || taskIds.isEmpty()) {
            log.warn("taskIds is null or empty for courseId: {}", courseId);
            return;
        }

        Set<TaskEntity> tasksToAdd = taskService.getTasksByIdsWithCourses(taskIds);

        if (!tasksToAdd.isEmpty()) {
            for (TaskEntity task : tasksToAdd) {
                currentCourse.getTasks().add(task);
                task.getCourses().add(currentCourse);
            }
            courseService.save(currentCourse);

            log.info("Added {} tasks to course ID: {}", tasksToAdd.size(), courseId);
        } else {
            log.warn("No tasks found for provided taskIds: {}", taskIds);
        }
    }

    public CourseInfo createCourse(CourseCreateDto courseCreateDto, String userId) {
        if (courseCreateDto == null || userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("Invalid input data");
        }

        CourseEntity course = courseMapper.mapToCourseEntity(courseCreateDto);
        course.setAuthorId(userId);

        Set<TaskEntity> tasks = new HashSet<>();
        if (courseCreateDto.taskIds() != null && !courseCreateDto.taskIds().isEmpty()) {
            tasks = taskService.getTasksByIds(courseCreateDto.taskIds());
        }

        if (!tasks.isEmpty()) {
            course.setTasks(tasks);
            for (TaskEntity task : tasks) {
                task.getCourses().add(course);
            }
        }

        CourseEntity savedCourse = courseService.save(course);

        return courseMapper.mapToCourseInfo(savedCourse);
    }

}
