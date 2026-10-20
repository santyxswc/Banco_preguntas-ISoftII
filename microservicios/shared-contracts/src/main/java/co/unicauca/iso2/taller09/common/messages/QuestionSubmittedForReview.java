package co.unicauca.iso2.taller09.common.messages;

import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import java.io.Serializable;
import java.time.LocalDateTime;

public class QuestionSubmittedForReview implements Serializable {
    private String operationId;
    private Long questionId;
    private QuestionStatus newState;
    private LocalDateTime occurredAt;

    public QuestionSubmittedForReview() {}

    public QuestionSubmittedForReview(String operationId, Long questionId, QuestionStatus newState, LocalDateTime occurredAt) {
        this.operationId = operationId;
        this.questionId = questionId;
        this.newState = newState;
        this.occurredAt = occurredAt;
    }

    public String getOperationId() { return operationId; }
    public void setOperationId(String operationId) { this.operationId = operationId; }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public QuestionStatus getNewState() { return newState; }
    public void setNewState(QuestionStatus newState) { this.newState = newState; }

    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
}
