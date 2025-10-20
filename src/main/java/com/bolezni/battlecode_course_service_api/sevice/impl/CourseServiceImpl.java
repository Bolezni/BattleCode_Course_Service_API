package com.bolezni.battlecode_course_service_api.sevice.impl;

import com.bolezni.battlecode_course_service_api.dto.course.CourseInfo;
import com.bolezni.battlecode_course_service_api.dto.course.CourseUpdate;
import com.bolezni.battlecode_course_service_api.mapper.CourseMapper;
import com.bolezni.battlecode_course_service_api.model.CourseEntity;
import com.bolezni.battlecode_course_service_api.model.CourseLevel;
import com.bolezni.battlecode_course_service_api.model.TaskEntity;
import com.bolezni.battlecode_course_service_api.repository.CourseRepository;
import com.bolezni.battlecode_course_service_api.sevice.CourseService;
import com.bolezni.battlecode_course_service_api.sevice.TaskService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CourseServiceImpl implements CourseService {
    CourseRepository courseRepository;
    CourseMapper courseMapper;
    TaskService taskService;

    @Override
    @Transactional
    public CourseEntity save(CourseEntity courseEntity) {
        return courseRepository.save(courseEntity);
    }

    @Override
    public CourseEntity getCourseEntity(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Course not found");
                    return new IllegalArgumentException("Course with id " + id + " not found");
                });
    }

    @Override
    public CourseInfo getCourse(Long id, boolean withTasks) {
        if(withTasks) {
           return courseMapper.mapToCourseInfo(getCourseEntityWithTasks(id));
        }else {
            return courseMapper.mapToCourseInfo(getCourseEntity(id));
        }
    }

    @Override
    public CourseEntity getCourseEntityWithTasks(Long id) {
        return courseRepository.findByIdWithTasks(id)
                .orElseThrow(() -> {
                    log.error("Course not found: {}", id);
                    return new IllegalArgumentException("Course with id " + id + " not found");
                });
    }

    @Override
    @Transactional
    public void deleteCourse(Long id, String authorId) {
        if (id == null) {
            log.error("Course ID cannot be null");
            throw new IllegalArgumentException("Course ID cannot be null");
        }

        CourseEntity course = getCourseEntity(id);

        if (!course.getAuthorId().equals(authorId)) {
            log.warn("User {} attempted to delete course {} authored by {}",
                    authorId, id, course.getAuthorId());
            throw new IllegalArgumentException("Only course author can delete this course");
        }

        courseRepository.deleteById(id);
        log.info("Course with ID {} deleted by user {} successfully", id, authorId);
    }

    @Override
    @Transactional
    public CourseInfo updateCourse(Long id, CourseUpdate updateDto, String userId) {
        if (id == null || updateDto == null) {
            log.error("Bad request: id or updateDto is null");
            throw new IllegalArgumentException("Bad request");
        }

        CourseEntity course = getCourseEntity(id);

        if (!course.getAuthorId().equals(userId)) {
            log.warn("User {} attempted to update course {} authored by {}",
                    userId, id, course.getAuthorId());
            throw new IllegalArgumentException("Only course author can update this course");
        }

        updateBasicCourseFields(course, updateDto);
        updateCourseTasks(course, updateDto.taskIdsToAdd(), updateDto.taskIdsToRemove());

        CourseEntity updatedCourse = courseRepository.save(course);
        log.info("Course with ID {} updated by user {} successfully", id, userId);

        return courseMapper.mapToCourseInfo(updatedCourse);
    }

    @Override
    public Set<CourseEntity> getAllCoursesByCoursesId(Set<Long> ids) {
        return courseRepository.findAllByIdIn(ids);
    }

    @Override
    @Transactional
    public void addTaskToCourses(TaskEntity task, Set<Long> courseIds) {
        if (courseIds != null && !courseIds.isEmpty()) {
            Set<CourseEntity> courses = getAllCoursesByCoursesId(courseIds);
            for (CourseEntity course : courses) {
                course.getTasks().add(task);
            }
            courseRepository.saveAll(courses);
        }
    }

    @Override
    public Page<CourseInfo> getAllCourses(Pageable pageable) {

        Page<CourseEntity> page = courseRepository.findAll(pageable);

        List<Long> courseIds = page.getContent().stream()
                .map(CourseEntity::getId)
                .toList();

        if(courseIds.isEmpty()){
            return Page.empty(pageable);
        }

        List<CourseEntity> courseWithDetails = courseRepository.findByIdInWithDetails(courseIds);

        Map<Long, CourseEntity> courseMap = courseWithDetails.stream()
                .collect(Collectors.toMap(CourseEntity::getId, Function.identity()));

        List<CourseInfo> courseInfos = page.getContent().stream()
                .map(course -> courseMap.getOrDefault(course.getId(), course))
                .map(courseMapper::mapToCourseInfo)
                .toList();

        return new PageImpl<>(courseInfos, pageable, page.getTotalPages());
    }

    private void updateBasicCourseFields(CourseEntity course, CourseUpdate updateDto) {
        if (updateDto.title() != null && !updateDto.title().isBlank()) {
            course.setTitle(updateDto.title());
        }

        if (updateDto.description() != null && !updateDto.description().isBlank()) {
            course.setDescription(updateDto.description());
        }

        if (updateDto.imageUrl() != null && !updateDto.imageUrl().isBlank()) {
            course.setImageUrl(updateDto.imageUrl());
        }

        if (updateDto.level() != null && !updateDto.level().isBlank()) {
            try {
                CourseLevel courseLevel = CourseLevel.valueOf(updateDto.level().toUpperCase());
                course.setLevel(courseLevel);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid course level: {}", updateDto.level());
                throw new IllegalArgumentException("Invalid course level: " + updateDto.level());
            }
        }

        if (updateDto.price() != null && updateDto.price() >= 0) {
            course.setPrice(updateDto.price());
        }

        if (updateDto.isPublished() != null) {
            course.setIsPublished(updateDto.isPublished());
        }
    }

    private void updateCourseTasks(CourseEntity course, Set<Long> taskIdsToAdd, Set<Long> taskIdsToRemove) {
        Set<TaskEntity> currentTasks = course.getTasks();

        if (taskIdsToRemove != null && !taskIdsToRemove.isEmpty()) {
            Set<TaskEntity> tasksToRemove = taskService.getTasksByIds(taskIdsToRemove);
            currentTasks.removeAll(tasksToRemove);
            log.debug("Removed {} tasks from course {}", tasksToRemove.size(), course.getId());
        }

        if (taskIdsToAdd != null && !taskIdsToAdd.isEmpty()) {
            Set<TaskEntity> tasksToAdd = taskService.getTasksByIds(taskIdsToAdd);

            tasksToAdd.removeAll(currentTasks);
            currentTasks.addAll(tasksToAdd);

            log.debug("Added {} tasks to course {}", tasksToAdd.size(), course.getId());
        }

        course.setTasks(currentTasks);
    }

}
