package co.unicauca.iso2.taller09.common.messages;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Evento asíncrono ReviewerAssigned (Publicado por Review Service -> Consumido por Notification Service).
 */
public class ReviewerAssigned implements Serializable {
    private String operationId;
    private Long questionId;
    private Long reviewerId;
    private Long assignedBy;
    private LocalDateTime occurredAt;

    public ReviewerAssigned() {}

    public ReviewerAssigned(String operationId, Long questionId, Long reviewerId, Long assignedBy, LocalDateTime occurredAt) {
        this.operationId = operationId;
        this.questionId = questionId;
        this.reviewerId = reviewerId;
        this.assignedBy = assignedBy;
        this.occurredAt = occurredAt;
    }

    public String getOperationId() { return operationId; }
    public void setOperationId(String operationId) { this.operationId = operationId; }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public Long getReviewerId() { return reviewerId; }
    public void setReviewerId(Long reviewerId) { this.reviewerId = reviewerId; }

    public Long getAssignedBy() { return assignedBy; }
    public void setAssignedBy(Long assignedBy) { this.assignedBy = assignedBy; }

    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
}
