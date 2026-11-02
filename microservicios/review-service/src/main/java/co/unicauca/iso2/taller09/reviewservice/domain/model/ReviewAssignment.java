package co.unicauca.iso2.taller09.reviewservice.domain.model;

import co.unicauca.iso2.taller09.common.enums.AssignmentStatus;
import co.unicauca.iso2.taller09.reviewservice.domain.exception.ReviewException;

import java.time.LocalDateTime;

public class ReviewAssignment {
    private Long id;
    private final Long questionId;
    private final Long reviewerId;
    private final Long assignedBy;
    private final int round;
    private final LocalDateTime assignedAt;
    private AssignmentStatus status;

    public ReviewAssignment(Long id, Long questionId, Long reviewerId, Long assignedBy, int round,
                            LocalDateTime assignedAt, AssignmentStatus status) {
        this.id = id;
        this.questionId = questionId;
        this.reviewerId = reviewerId;
        this.assignedBy = assignedBy;
        this.round = round;
        this.assignedAt = assignedAt;
        this.status = status;
    }

    public static ReviewAssignment create(Long questionId, Long reviewerId, Long assignedBy, int round) {
        if (round < 1) {
            throw ReviewException.validation("La ronda de revisión debe ser mayor o igual a 1.");
        }
        return new ReviewAssignment(null, questionId, reviewerId, assignedBy, round,
                LocalDateTime.now(), AssignmentStatus.ASSIGNED);
    }

    public boolean isPending() {
        return status == AssignmentStatus.ASSIGNED;
    }

    public void complete() {
        if (!isPending()) {
            throw ReviewException.conflict("La asignación ya no está pendiente de evaluación.");
        }
        status = AssignmentStatus.COMPLETED;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getQuestionId() { return questionId; }
    public Long getReviewerId() { return reviewerId; }
    public Long getAssignedBy() { return assignedBy; }
    public int getRound() { return round; }
    public LocalDateTime getAssignedAt() { return assignedAt; }
    public AssignmentStatus getStatus() { return status; }
}
