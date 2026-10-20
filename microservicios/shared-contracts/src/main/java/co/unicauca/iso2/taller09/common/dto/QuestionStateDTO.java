package co.unicauca.iso2.taller09.common.dto;

import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import java.io.Serializable;
import java.time.LocalDateTime;

public class QuestionStateDTO implements Serializable {
    private Long id;
    private Long questionId;
    private QuestionStatus state;
    private LocalDateTime changedAt;
    private String changedBy;
    private String message;

    public QuestionStateDTO() {}

    public QuestionStateDTO(Long id, Long questionId, QuestionStatus state, LocalDateTime changedAt, String changedBy, String message) {
        this.id = id;
        this.questionId = questionId;
        this.state = state;
        this.changedAt = changedAt;
        this.changedBy = changedBy;
        this.message = message;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public QuestionStatus getState() { return state; }
    public void setState(QuestionStatus state) { this.state = state; }

    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }

    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
