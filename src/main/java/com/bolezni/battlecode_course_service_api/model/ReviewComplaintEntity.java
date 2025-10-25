package com.bolezni.battlecode_course_service_api.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "review_complaints")
@Entity
public class ReviewComplaintEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "complain_seq")
    @SequenceGenerator(sequenceName = "complain_sequence_id", name = "complain_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private PeerReviewEntity review;

    @Column(name = "complainant_id", nullable = false)
    private String complainantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintReason reason;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ComplaintStatus status = ComplaintStatus.PENDING;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "resolved_by")
    private String resolvedBy;

    @Column(columnDefinition = "TEXT")
    private String resolutionNote;

    public enum ComplaintReason {
        SPAM,                    // Спам
        INAPPROPRIATE_LANGUAGE,  // Неподобающий язык
        UNFAIR_EVALUATION,      // Несправедливая оценка
        PERSONAL_ATTACK,        // Личные нападки
        OTHER                   // Другое
    }

    public enum ComplaintStatus {
        PENDING,         // Ожидает рассмотрения
        UNDER_REVIEW,    // На рассмотрении
        ACCEPTED,        // Принята (отзыв будет скрыт/удалён)
        REJECTED,        // Отклонена
        CLOSED           // Закрыта
    }
}
