package com.bolezni.battlecode_course_service_api.controller;


import com.bolezni.battlecode_course_service_api.dto.UserInfo;
import com.bolezni.battlecode_course_service_api.dto.course.CourseCreateDto;
import com.bolezni.battlecode_course_service_api.dto.course.CourseInfo;
import com.bolezni.battlecode_course_service_api.dto.course.CourseUpdate;
import com.bolezni.battlecode_course_service_api.security.CurrentUser;
import com.bolezni.battlecode_course_service_api.security.JwtAuth;
import com.bolezni.battlecode_course_service_api.sevice.CourseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "course_methods")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/course")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CourseController {
    CourseService courseService;

    @PostMapping
    @JwtAuth
    public ResponseEntity<CourseInfo> createCourse(
            @RequestBody CourseCreateDto request,
            @CurrentUser UserInfo userInfo) {

        CourseInfo course = courseService.createCourse(request, userInfo.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(course);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseInfo> getCourse(@PathVariable Long id) {
        CourseInfo course = courseService.getCourse(id);
        return ResponseEntity.ok(course);
    }

    @PutMapping("/{id}")
    @JwtAuth
    public ResponseEntity<CourseInfo> updateCourse(
            @PathVariable Long id,
            @RequestBody CourseUpdate updateDto,
            @CurrentUser UserInfo userInfo) {

        CourseInfo course = courseService.updateCourse(id, updateDto, userInfo.userId());
        return ResponseEntity.ok(course);
    }

    @DeleteMapping("/{id}")
    @JwtAuth
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long id,
            @CurrentUser UserInfo userInfo) {

        courseService.deleteCourse(id, userInfo.userId());
        return ResponseEntity.noContent().build();
    }
}
