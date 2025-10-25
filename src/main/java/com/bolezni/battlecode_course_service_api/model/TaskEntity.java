package com.bolezni.battlecode_course_service_api.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tasks")
@Entity
public class TaskEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_seq")
    @SequenceGenerator(name = "task_seq", sequenceName = "task_sequence_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String initialCode;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String testCases;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskDifficult difficulty; // EASY, MEDIUM, HARD

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskType type; // THEORY, PRACTICE, PROJECT

    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(name = "estimated_minutes")
    private Integer estimatedMinutes;

    @Column(columnDefinition = "TEXT")
    private String content; // HTML/content

    @ManyToMany(mappedBy = "tasks",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @BatchSize(size = 20)
    @Builder.Default
    private Set<CourseEntity> courses = new HashSet<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<TaskSubmissionEntity> submissions = new HashSet<>();
}