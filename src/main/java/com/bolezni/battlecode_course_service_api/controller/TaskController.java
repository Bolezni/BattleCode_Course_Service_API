package com.bolezni.battlecode_course_service_api.controller;

import com.bolezni.battlecode_course_service_api.dto.task.TaskCreateDto;
import com.bolezni.battlecode_course_service_api.dto.task.TaskInfo;
import com.bolezni.battlecode_course_service_api.dto.task.TaskUpdateDto;
import com.bolezni.battlecode_course_service_api.facade.TaskFacade;
import com.bolezni.battlecode_course_service_api.security.JwtAuth;
import com.bolezni.battlecode_course_service_api.sevice.TaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@JwtAuth
@Tag(name = "task_methods")
@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TaskController {

    TaskFacade taskFacade;
    TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskInfo> createTask(@Valid @RequestBody TaskCreateDto taskCreateDto) {
        TaskInfo dto = taskFacade.createNewTask(taskCreateDto);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskInfo> updateTask(@PathVariable(name = "id") Long id,
                                               @Valid @RequestBody TaskUpdateDto taskUpdateDto) {
        TaskInfo dto = taskService.updateTask(id, taskUpdateDto);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable(name = "id") Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tasks")
    public ResponseEntity<Set<TaskInfo>> getAllTasksById(@NotNull @RequestBody Set<Long> taskIds) {
        Set<TaskInfo> dto = taskService.getTasksInfoByIds(taskIds);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/set")
    public ResponseEntity<Set<TaskInfo>> getTasksPage(@RequestParam(name = "course_id") Long courseId) {
        Set<TaskInfo> dto = taskService.getCourseTasks(courseId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<TaskInfo>> getTasksPage(@RequestParam(name = "course_id") Long courseId,
                                                       @PageableDefault(size = 20, sort = "orderIndex", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<TaskInfo> dto = taskService.getCourseTasks(courseId, pageable);
        return ResponseEntity.ok(dto);
    }


}
