package co.unicauca.iso2.taller09.common.messages;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Evento asíncrono ReviewerAssigned (Publicado por Review Service -> Consumido por Notification Service).
 * <p>
 * {@code round} es la ronda de revisión que se abre con la asignación: 1 para la primera
 * revisión y 2 o más cuando el autor reenvió una versión mejorada (HU-06).
 */
public class ReviewerAssigned implements Serializable {
    private String operationId;
    private Long questionId;
    private Long reviewerId;
    private Long assignedBy;
    private int round = 1;
    private LocalDateTime occurredAt;

    public ReviewerAssigned() {}

    public ReviewerAssigned(String operationId, Long questionId, Long reviewerId, Long assignedBy, LocalDateTime occurredAt) {
        this.operationId = operationId;
        this.questionId = questionId;
        this.reviewerId = reviewerId;
        this.assignedBy = assignedBy;
        this.occurredAt = occurredAt;
    }

    public ReviewerAssigned(String operationId, Long questionId, Long reviewerId, Long assignedBy, int round,
                            LocalDateTime occurredAt) {
        this(operationId, questionId, reviewerId, assignedBy, occurredAt);
        this.round = round;
    }

    public String getOperationId() { return operationId; }
    public void setOperationId(String operationId) { this.operationId = operationId; }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public Long getReviewerId() { return reviewerId; }
    public void setReviewerId(Long reviewerId) { this.reviewerId = reviewerId; }

    public Long getAssignedBy() { return assignedBy; }
    public void setAssignedBy(Long assignedBy) { this.assignedBy = assignedBy; }

    public int getRound() { return round; }
    public void setRound(int round) { this.round = round; }

    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
}
