package com.bolezni.battlecode_course_service_api.sevice;


import com.bolezni.battlecode_course_service_api.dto.course.CourseInfo;
import com.bolezni.battlecode_course_service_api.dto.course.CourseUpdate;
import com.bolezni.battlecode_course_service_api.model.CourseEntity;
import com.bolezni.battlecode_course_service_api.model.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface CourseService {

    CourseEntity getCourseEntity(Long id);

    CourseInfo getCourse(Long id, boolean withTasks);

    CourseEntity getCourseEntityWithTasks(Long id);

    void deleteCourse(Long id, String authorId);

    CourseInfo updateCourse(Long id, CourseUpdate updateDto, String userId);

    Set<CourseEntity> getAllCoursesByCoursesId(Set<Long> ids);

    void addTaskToCourses(TaskEntity task, Set<Long> courseIds);

    CourseEntity save(CourseEntity courseEntity);

    Page<CourseInfo> getAllCourses(Pageable pageable);

}
