package com.bolezni.battlecode_course_service_api.mapper;

import com.bolezni.battlecode_course_service_api.dto.course.CourseCreateDto;
import com.bolezni.battlecode_course_service_api.dto.course.CourseInfo;
import com.bolezni.battlecode_course_service_api.model.CourseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TaskMapper.class})
public interface CourseMapper {

    CourseInfo mapToCourseInfo(CourseEntity courseEntity);

    @Mapping(target = "tasks", source = "taskIds", ignore = true)
    CourseEntity mapToCourseEntity(CourseCreateDto createDto);
}
