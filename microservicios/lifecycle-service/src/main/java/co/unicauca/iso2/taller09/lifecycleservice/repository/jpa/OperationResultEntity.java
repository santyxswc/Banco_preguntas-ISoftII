package co.unicauca.iso2.taller09.lifecycleservice.repository.jpa;

import co.unicauca.iso2.taller09.common.enums.OperationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/** Fila de {@code operation_result}: resultado de una operación asíncrona, identificada por su operationId. */
@Entity
@Table(name = "operation_result")
public class OperationResultEntity {

    @Id
    @Column(name = "id", length = 64)
    private String id;

    @Column(name = "question_id")
    private Long questionId;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OperationStatus status;

    @Column(name = "message", length = 1000)
    private String message;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    protected OperationResultEntity() {
    }

    OperationResultEntity(String id) {
        this.id = id;
    }

    String getId() { return id; }
    Long getQuestionId() { return questionId; }
    void setQuestionId(Long questionId) { this.questionId = questionId; }
    String getType() { return type; }
    void setType(String type) { this.type = type; }
    OperationStatus getStatus() { return status; }
    void setStatus(OperationStatus status) { this.status = status; }
    String getMessage() { return message; }
    void setMessage(String message) { this.message = message; }
    LocalDateTime getCreatedAt() { return createdAt; }
    void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    LocalDateTime getCompletedAt() { return completedAt; }
    void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
