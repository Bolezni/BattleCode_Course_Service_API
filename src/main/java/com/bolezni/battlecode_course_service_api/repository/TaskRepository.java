package com.bolezni.battlecode_course_service_api.repository;

import com.bolezni.battlecode_course_service_api.model.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    Set<TaskEntity> findAllByIdIn(Set<Long> ids);

    @Query("SELECT t FROM TaskEntity t JOIN t.courses c WHERE c.id = :courseId ORDER BY t.orderIndex")
    Set<TaskEntity> findAllByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT t FROM TaskEntity t JOIN t.courses c WHERE c.id = :courseId ORDER BY t.orderIndex")
    Page<TaskEntity> findAllByCourseId(@Param("courseId") Long courseId, Pageable pageable);

    @Query("SELECT t FROM TaskEntity t JOIN t.courses c WHERE c.id = :courseId AND t.orderIndex > :currentOrderIndex ORDER BY t.orderIndex")
    TaskEntity findNextTaskByCourseIdAndCurrentOrder(@Param("courseId") Long courseId, @Param("currentOrderIndex") Integer currentOrderIndex);

    @EntityGraph(attributePaths = {"courses"})
    @Query("SELECT t FROM TaskEntity t WHERE t.id IN :ids")
    Set<TaskEntity> findByIdInWithCourses(@Param("ids") Set<Long> ids);
}
