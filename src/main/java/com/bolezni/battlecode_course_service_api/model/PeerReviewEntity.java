package com.bolezni.battlecode_course_service_api.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "peer_review",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"submission_id", "reviewerId"})})
public class PeerReviewEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "review_seq")
    @SequenceGenerator(name = "review_seq", sequenceName = "review_sequence_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private TaskSubmissionEntity submission;

    @Column(name = "reviewer_id", nullable = false)
    private String reviewerId;

    // Оценки по 5 критериям (например, от 1 до 5)
    @Column(nullable = false)
    private Integer readability; // Читаемость

    @Column(nullable = false)
    private Integer efficiency; // Эффективность

    @Column(nullable = false)
    private Integer scalability; // Масштабируемость

    @Column(nullable = false)
    private Integer reliability; // Надёжность

    @Column(nullable = false)
    private Integer maintainability; // Сопровождаемость

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ReviewStatus status = ReviewStatus.ACTIVE;

    @Column(name = "reviewed_at", nullable = false)
    private LocalDateTime reviewedAt;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<ReviewComplaintEntity> complaints = new HashSet<>();

    public enum ReviewStatus {
        ACTIVE,          // Активный отзыв
        FLAGGED,         // Отмечен (есть жалоба)
        HIDDEN,          // Скрыт модератором
        DELETED          // Удалён
    }
}
