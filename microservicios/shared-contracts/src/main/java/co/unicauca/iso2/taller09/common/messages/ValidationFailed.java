package co.unicauca.iso2.taller09.common.messages;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class ValidationFailed implements Serializable {
    private String operationId;
    private Long questionId;
    private List<String> errors;
    private LocalDateTime occurredAt;

    public ValidationFailed() {}

    public ValidationFailed(String operationId, Long questionId, List<String> errors, LocalDateTime occurredAt) {
        this.operationId = operationId;
        this.questionId = questionId;
        this.errors = errors;
        this.occurredAt = occurredAt;
    }

    public String getOperationId() { return operationId; }
    public void setOperationId(String operationId) { this.operationId = operationId; }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }

    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
}
