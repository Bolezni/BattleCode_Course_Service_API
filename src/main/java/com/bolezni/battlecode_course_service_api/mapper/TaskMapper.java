package com.bolezni.battlecode_course_service_api.mapper;

import com.bolezni.battlecode_course_service_api.dto.course.CourseInfo;
import com.bolezni.battlecode_course_service_api.dto.task.TaskCreateDto;
import com.bolezni.battlecode_course_service_api.dto.task.TaskInfo;
import com.bolezni.battlecode_course_service_api.dto.task.TaskUpdateDto;
import com.bolezni.battlecode_course_service_api.model.CourseEntity;
import com.bolezni.battlecode_course_service_api.model.TaskEntity;
import org.mapstruct.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "courses", source = "courses", qualifiedByName = "mapCourses")
    TaskInfo mapToTaskInfo(TaskEntity taskEntity);

    TaskEntity mapToTaskEntity(TaskCreateDto createDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTaskEntity(TaskUpdateDto dto, @MappingTarget TaskEntity entity);

    @Named("mapCourses")
    default Set<CourseInfo> mapCourses(Set<CourseEntity> courses) {
        if (courses == null) {
            return new HashSet<>();
        }
        return courses.stream()
                .map(course -> CourseInfo.builder()
                        .id(course.getId())
                        .title(course.getTitle())
                        .description(course.getDescription())
                        .imageUrl(course.getImageUrl())
                        .level(course.getLevel().name())
                        .price(course.getPrice())
                        .isPublished(course.getIsPublished())
                        .authorId(course.getAuthorId())
                        .studentCount(course.getStudentCount())
                        .createdAt(course.getCreatedAt())
                        .updatedAt(course.getUpdatedAt())
                        .build())
                .collect(Collectors.toSet());
    }
}
