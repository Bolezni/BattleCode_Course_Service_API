package com.bolezni.battlecode_course_service_api.controller;

import com.bolezni.battlecode_course_service_api.dto.UserInfo;
import com.bolezni.battlecode_course_service_api.dto.task.SaveDraftRequest;
import com.bolezni.battlecode_course_service_api.dto.task.SubmitTaskRequest;
import com.bolezni.battlecode_course_service_api.dto.task.TaskSubmissionInfo;
import com.bolezni.battlecode_course_service_api.facade.TaskFacade;
import com.bolezni.battlecode_course_service_api.security.JwtAuth;
import com.bolezni.battlecode_course_service_api.sevice.TaskSubmissionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "peer_reviews_methods")
@JwtAuth
@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TaskSubmissionController {

    TaskSubmissionService taskSubmissionService;
    TaskFacade taskFacade;

    /**
     * Отправить решение задачи
     */
    @PostMapping
    public ResponseEntity<TaskSubmissionInfo> submitSolution(
            HttpServletRequest httpRequest,
            @Valid @RequestBody SubmitTaskRequest request) {
        UserInfo userInfo = (UserInfo) httpRequest.getAttribute("userInfo");

        TaskSubmissionInfo submission = taskSubmissionService.submitSolution(userInfo.userId(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(submission);
    }

    /**
     * Опубликовать решение
     */
    @PostMapping("/{submissionId}/publish")
    public ResponseEntity<TaskSubmissionInfo> publishSubmission(
            HttpServletRequest httpRequest,
            @PathVariable Long submissionId) {
        UserInfo userInfo = (UserInfo) httpRequest.getAttribute("userInfo");

        TaskSubmissionInfo submission = taskSubmissionService.publishSubmission(submissionId, userInfo.userId());

        return ResponseEntity.ok(submission);
    }

    @GetMapping("/{submissionId}")
    public ResponseEntity<TaskSubmissionInfo> getSubmission(
            HttpServletRequest httpRequest,
            @PathVariable Long submissionId) {

        UserInfo userInfo = (UserInfo) httpRequest.getAttribute("userInfo");

        TaskSubmissionInfo submission = taskSubmissionService.getSubmission(userInfo.userId(), submissionId);

        return ResponseEntity.ok(submission);
    }

    /**
     * Создание черновика
     */
    @PostMapping("/submissions/draft")
    public ResponseEntity<TaskSubmissionInfo> createDraft(@Valid @RequestBody SaveDraftRequest request, HttpServletRequest httpServletRequest) {
        UserInfo userInfo = (UserInfo) httpServletRequest.getAttribute("userInfo");

        TaskSubmissionInfo dto = taskFacade.saveDraft(userInfo.userId(), request);
        return ResponseEntity.ok(dto);
    }
    /**
     * Получение решений, доступных для рецензирования по задаче
     */
    @GetMapping("/tasks/{taskId}/available")
    public ResponseEntity<List<TaskSubmissionInfo>> getAvailableSubmissions(
            @PathVariable Long taskId, HttpServletRequest request) {
        UserInfo userInfo = (UserInfo) request.getAttribute("userInfo");
        List<TaskSubmissionInfo> submissions = taskSubmissionService.getSubmissionsForReview(userInfo.userId(), taskId);
        return ResponseEntity.ok(submissions);
    }

    /**
     * Получение решений пользователя с их отзывами
     */
    @GetMapping("/my")
    public ResponseEntity<List<TaskSubmissionInfo>> getMySubmissions(HttpServletRequest httpRequest) {

        UserInfo userInfo = (UserInfo) httpRequest.getAttribute("userInfo");
        List<TaskSubmissionInfo> submissions = taskSubmissionService.getSubmissionsByUser(userInfo.userId());
        return ResponseEntity.ok(submissions);
    }
}
