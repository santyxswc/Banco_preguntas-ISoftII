package co.unicauca.iso2.taller09.reviewservice.domain;

import co.unicauca.iso2.taller09.common.dto.ReviewAssignmentDTO;
import co.unicauca.iso2.taller09.common.enums.AssignmentStatus;

import java.time.LocalDateTime;

public class ReviewAssignment {
    private Long id;
    private Long questionId;
    private Long reviewerId;
    private Long assignedBy;
    private LocalDateTime assignedAt;
    private AssignmentStatus status;

    public ReviewAssignment() {}

    public ReviewAssignment(Long id, Long questionId, Long reviewerId, Long assignedBy) {
        this.id = id;
        this.questionId = questionId;
        this.reviewerId = reviewerId;
        this.assignedBy = assignedBy;
        this.assignedAt = LocalDateTime.now();
        this.status = AssignmentStatus.ASSIGNED;
    }

    public ReviewAssignmentDTO toDTO() {
        return new ReviewAssignmentDTO(id, questionId, reviewerId, assignedAt, status);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public Long getReviewerId() { return reviewerId; }
    public void setReviewerId(Long reviewerId) { this.reviewerId = reviewerId; }

    public Long getAssignedBy() { return assignedBy; }
    public void setAssignedBy(Long assignedBy) { this.assignedBy = assignedBy; }

    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }

    public AssignmentStatus getStatus() { return status; }
    public void setStatus(AssignmentStatus status) { this.status = status; }
}
