package co.unicauca.iso2.taller09.common.dto;

import co.unicauca.iso2.taller09.common.enums.OperationStatus;
import java.io.Serializable;
import java.time.LocalDateTime;

public class OperationResultDTO implements Serializable {
    private String id;
    private Long questionId;
    private String type;
    private OperationStatus status;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public OperationResultDTO() {}

    public OperationResultDTO(String id, Long questionId, String type, OperationStatus status, String message, LocalDateTime createdAt, LocalDateTime completedAt) {
        this.id = id;
        this.questionId = questionId;
        this.type = type;
        this.status = status;
        this.message = message;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public OperationStatus getStatus() { return status; }
    public void setStatus(OperationStatus status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
