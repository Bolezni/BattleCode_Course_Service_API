package com.bolezni.battlecode_course_service_api.controller;


import com.bolezni.battlecode_course_service_api.dto.AddTasksRequest;
import com.bolezni.battlecode_course_service_api.dto.UserInfo;
import com.bolezni.battlecode_course_service_api.dto.course.CourseCreateDto;
import com.bolezni.battlecode_course_service_api.dto.course.CourseInfo;
import com.bolezni.battlecode_course_service_api.dto.course.CourseUpdate;
import com.bolezni.battlecode_course_service_api.facade.CourseFacade;
import com.bolezni.battlecode_course_service_api.security.JwtAuth;
import com.bolezni.battlecode_course_service_api.sevice.CourseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "course_methods")
@RestController
@JwtAuth
@RequiredArgsConstructor
@RequestMapping("/api/course")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CourseController {
    CourseService courseService;
    CourseFacade courseFacade;

    @PostMapping
    public ResponseEntity<CourseInfo> createCourse(
            @RequestBody CourseCreateDto request, HttpServletRequest httpRequest) {

        UserInfo userInfo = (UserInfo) httpRequest.getAttribute("userInfo");
        log.debug(userInfo.toString());


        CourseInfo course = courseFacade.createCourse(request, userInfo.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(course);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseInfo> getCourse(@PathVariable Long id) {
        CourseInfo course = courseService.getCourse(id, true);
        return ResponseEntity.ok(course);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<CourseInfo>> getAllCourses(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(courseService.getAllCourses(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseInfo> updateCourse(
            @PathVariable Long id,
            @RequestBody CourseUpdate updateDto,
            HttpServletRequest httpRequest) {
        UserInfo userInfo = (UserInfo) httpRequest.getAttribute("userInfo");
        CourseInfo course = courseService.updateCourse(id, updateDto, userInfo.userId());
        return ResponseEntity.ok(course);
    }

    @PostMapping("/{id}/add-tasks")
    public ResponseEntity<Void> addTasks(@Positive @PathVariable(name = "id") Long courseId,
                                         @RequestBody AddTasksRequest request) {
        courseFacade.addTasksToCourse(courseId, request.getTaskIds());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        UserInfo userInfo = (UserInfo) httpRequest.getAttribute("userInfo");
        courseService.deleteCourse(id, userInfo.userId());
        return ResponseEntity.noContent().build();
    }
}
