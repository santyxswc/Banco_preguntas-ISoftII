package co.unicauca.iso2.taller09.lifecycleservice.repository.jpa;

import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/** Fila de {@code question_state}: el estado vigente de una pregunta (una fila por pregunta). */
@Entity
@Table(name = "question_state")
public class QuestionStateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_id", nullable = false, unique = true)
    private Long questionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 20)
    private QuestionStatus state;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @Column(name = "changed_by", length = 200)
    private String changedBy;

    @Column(name = "message", length = 1000)
    private String message;

    protected QuestionStateEntity() {
    }

    QuestionStateEntity(Long questionId) {
        this.questionId = questionId;
    }

    Long getId() { return id; }
    Long getQuestionId() { return questionId; }
    QuestionStatus getState() { return state; }
    void setState(QuestionStatus state) { this.state = state; }
    LocalDateTime getChangedAt() { return changedAt; }
    void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }
    String getChangedBy() { return changedBy; }
    void setChangedBy(String changedBy) { this.changedBy = changedBy; }
    String getMessage() { return message; }
    void setMessage(String message) { this.message = message; }
}
