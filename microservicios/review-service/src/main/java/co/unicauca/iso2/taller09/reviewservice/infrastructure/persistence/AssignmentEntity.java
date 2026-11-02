package co.unicauca.iso2.taller09.reviewservice.infrastructure.persistence;

import co.unicauca.iso2.taller09.common.enums.AssignmentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "asignacion")
public class AssignmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "reviewer_id", nullable = false)
    private Long reviewerId;

    @Column(name = "assigned_by", nullable = false)
    private Long assignedBy;

    @Column(name = "ronda", nullable = false)
    private int round;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AssignmentStatus status;

    protected AssignmentEntity() {
    }

    public AssignmentEntity(Long id, Long questionId, Long reviewerId, Long assignedBy, int round,
                            LocalDateTime assignedAt, AssignmentStatus status) {
        this.id = id;
        this.questionId = questionId;
        this.reviewerId = reviewerId;
        this.assignedBy = assignedBy;
        this.round = round;
        this.assignedAt = assignedAt;
        this.status = status;
    }

    public Long getId() { return id; }
    public Long getQuestionId() { return questionId; }
    public Long getReviewerId() { return reviewerId; }
    public Long getAssignedBy() { return assignedBy; }
    public int getRound() { return round; }
    public LocalDateTime getAssignedAt() { return assignedAt; }
    public AssignmentStatus getStatus() { return status; }
}
