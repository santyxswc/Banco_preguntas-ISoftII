package co.unicauca.iso2.taller09.common.dto;

import co.unicauca.iso2.taller09.common.enums.AssignmentStatus;
import java.io.Serializable;
import java.time.LocalDateTime;

public class ReviewAssignmentDTO implements Serializable {
    private Long id;
    private Long questionId;
    private Long reviewerId;
    private LocalDateTime assignedAt;
    private AssignmentStatus status;

    public ReviewAssignmentDTO() {}

    public ReviewAssignmentDTO(Long id, Long questionId, Long reviewerId, LocalDateTime assignedAt, AssignmentStatus status) {
        this.id = id;
        this.questionId = questionId;
        this.reviewerId = reviewerId;
        this.assignedAt = assignedAt;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public Long getReviewerId() { return reviewerId; }
    public void setReviewerId(Long reviewerId) { this.reviewerId = reviewerId; }

    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }

    public AssignmentStatus getStatus() { return status; }
    public void setStatus(AssignmentStatus status) { this.status = status; }
}
