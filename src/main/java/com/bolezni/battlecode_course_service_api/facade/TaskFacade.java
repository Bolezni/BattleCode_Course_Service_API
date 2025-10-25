package com.bolezni.battlecode_course_service_api.facade;

import com.bolezni.battlecode_course_service_api.dto.task.SaveDraftRequest;
import com.bolezni.battlecode_course_service_api.dto.task.TaskCreateDto;
import com.bolezni.battlecode_course_service_api.dto.task.TaskInfo;
import com.bolezni.battlecode_course_service_api.dto.task.TaskSubmissionInfo;
import com.bolezni.battlecode_course_service_api.mapper.TaskMapper;
import com.bolezni.battlecode_course_service_api.mapper.TaskSubmissionMapper;
import com.bolezni.battlecode_course_service_api.model.TaskEntity;
import com.bolezni.battlecode_course_service_api.model.TaskSubmissionEntity;
import com.bolezni.battlecode_course_service_api.sevice.CourseService;
import com.bolezni.battlecode_course_service_api.sevice.TaskService;
import com.bolezni.battlecode_course_service_api.sevice.TaskSubmissionService;
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
    TaskSubmissionService submissionService;
    TaskSubmissionMapper taskSubmissionMapper;

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

    public TaskSubmissionInfo saveDraft(String userId, SaveDraftRequest request) {
        validateRequest(userId, request);

        TaskEntity task = taskService.getById(request.taskId());

        TaskSubmissionEntity submission = submissionService
                .getOrCreateSubmission(task, userId, TaskSubmissionEntity.SubmissionStatus.DRAFT, request);

        TaskSubmissionEntity saved = submissionService.save(submission);

        return taskSubmissionMapper.mapToTaskSubmissionInfo(saved);
    }

    private void validateRequest(String userId, Object request) {
        if (userId.isEmpty() || request == null) {
            log.error("userId is null or empty");
            throw new IllegalArgumentException("userId is null or empty");
        }
    }
}
