package com.bolezni.battlecode_course_service_api.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "task_submissions")
@Builder
public class TaskSubmissionEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "submission_seq")
    @SequenceGenerator(name = "submission_seq", sequenceName = "submission_sequence_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private TaskEntity task;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SubmissionStatus status = SubmissionStatus.DRAFT;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "completed_reviews_count")
    @Builder.Default
    private Integer completedReviewsCount = 0;

    @Column(name = "required_reviews_count")
    @Builder.Default
    private Integer requiredReviewsCount = 3;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @BatchSize(size = 20)
    private Set<PeerReviewEntity> reviews = new HashSet<>();

    @Column(name = "avg_readability")
    private Double avgReadability;

    @Column(name = "avg_efficiency")
    private Double avgEfficiency;

    @Column(name = "avg_scalability")
    private Double avgScalability;

    @Column(name = "avg_reliability")
    private Double avgReliability;

    @Column(name = "avg_maintainability")
    private Double avgMaintainability;

    @Column(name = "overall_score")
    private Double overallScore;

    public enum SubmissionStatus {
        DRAFT,           // Черновик, не опубликован
        PUBLISHED,       // Опубликован, ожидает отзывы
        UNDER_REVIEW,    // В процессе рецензирования
        REVIEWED,        // Получил достаточно отзывов
        COMPLETED        // Полностью завершён
    }
}
