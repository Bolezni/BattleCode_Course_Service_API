package com.bolezni.battlecode_course_service_api.repository;

import com.bolezni.battlecode_course_service_api.model.CourseEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CourseRepository extends JpaRepository<CourseEntity, Long> {
    Set<CourseEntity> findAllByIdIn(Set<Long> ids);

    @EntityGraph(attributePaths = {"tasks"})
    @Query("SELECT c FROM CourseEntity c WHERE c.id = :id")
    Optional<CourseEntity> findByIdWithTasks(@Param("id") Long id);

    @EntityGraph(attributePaths = {"tasks", "subscriptions"})
    @Query("SELECT c FROM CourseEntity c WHERE c.id IN :ids")
    List<CourseEntity> findByIdInWithDetails(@Param("ids") List<Long> ids);
}
