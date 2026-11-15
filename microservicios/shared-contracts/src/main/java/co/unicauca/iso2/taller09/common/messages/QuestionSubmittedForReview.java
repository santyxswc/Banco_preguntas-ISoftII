package co.unicauca.iso2.taller09.common.messages;

import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Evento asíncrono QuestionSubmittedForReview (Publicado por Lifecycle Service -> Consumido por
 * Notification Service) cuando una pregunta queda pendiente de revisión.
 * <p>
 * {@code authorId} y {@code version} permiten avisar también al autor y distinguir el reenvío
 * de una versión mejorada (HU-06) del primer envío (HU-02).
 */
public class QuestionSubmittedForReview implements Serializable {
    private String operationId;
    private Long questionId;
    private QuestionStatus newState;
    private LocalDateTime occurredAt;
    private Long authorId;
    private int version = 1;

    public QuestionSubmittedForReview() {}

    public QuestionSubmittedForReview(String operationId, Long questionId, QuestionStatus newState, LocalDateTime occurredAt) {
        this.operationId = operationId;
        this.questionId = questionId;
        this.newState = newState;
        this.occurredAt = occurredAt;
    }

    public QuestionSubmittedForReview(String operationId, Long questionId, QuestionStatus newState, Long authorId,
                                      int version, LocalDateTime occurredAt) {
        this(operationId, questionId, newState, occurredAt);
        this.authorId = authorId;
        this.version = version;
    }

    /** Verdadero cuando lo que se envía es una versión mejorada de una pregunta ya revisada. */
    public boolean improvedVersion() {
        return version > 1;
    }

    public String getOperationId() { return operationId; }
    public void setOperationId(String operationId) { this.operationId = operationId; }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public QuestionStatus getNewState() { return newState; }
    public void setNewState(QuestionStatus newState) { this.newState = newState; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }

    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
}
