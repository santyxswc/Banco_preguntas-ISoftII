package co.unicauca.iso2.taller09.common.messages;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Comando asíncrono SubmitQuestionForReview (Publicado por Question Service -> Consumido por Lifecycle Service).
 * <p>
 * {@code version} es la versión de la pregunta que se envía: 1 en el primer envío y 2 o más
 * cuando el autor reenvía una versión mejorada tras un rechazo (HU-06).
 */
public class SubmitQuestionForReview implements Serializable {
    private String operationId;
    private Long questionId;
    private Long requestedBy;
    private LocalDateTime requestedAt;
    private int version = 1;

    public SubmitQuestionForReview() {}

    public SubmitQuestionForReview(String operationId, Long questionId, Long requestedBy, LocalDateTime requestedAt) {
        this.operationId = operationId;
        this.questionId = questionId;
        this.requestedBy = requestedBy;
        this.requestedAt = requestedAt;
    }

    public SubmitQuestionForReview(String operationId, Long questionId, Long requestedBy, int version,
                                   LocalDateTime requestedAt) {
        this(operationId, questionId, requestedBy, requestedAt);
        this.version = version;
    }

    public String getOperationId() { return operationId; }
    public void setOperationId(String operationId) { this.operationId = operationId; }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public Long getRequestedBy() { return requestedBy; }
    public void setRequestedBy(Long requestedBy) { this.requestedBy = requestedBy; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }

    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
}
