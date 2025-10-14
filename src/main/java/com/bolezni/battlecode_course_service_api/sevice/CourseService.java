package com.bolezni.battlecode_course_service_api.sevice;


import com.bolezni.battlecode_course_service_api.dto.course.CourseCreateDto;
import com.bolezni.battlecode_course_service_api.dto.course.CourseInfo;
import com.bolezni.battlecode_course_service_api.dto.course.CourseUpdate;

public interface CourseService {
    CourseInfo createCourse(CourseCreateDto courseCreateDto,String userId);

    CourseInfo getCourse(Long id);

    void deleteCourse(Long id, String authorId);

    CourseInfo updateCourse(Long id, CourseUpdate updateDto, String userId);
}
