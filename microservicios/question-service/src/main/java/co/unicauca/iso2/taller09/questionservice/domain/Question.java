package co.unicauca.iso2.taller09.questionservice.domain;

import co.unicauca.iso2.taller09.common.dto.OptionDTO;
import co.unicauca.iso2.taller09.common.dto.QuestionDTO;
import co.unicauca.iso2.taller09.common.enums.QuestionStatus;

import java.time.LocalDateTime;
import java.util.List;

public class Question {
    private Long id;
    private String text;
    private List<OptionDTO> options;
    private String correctOptionId;
    private Long authorId;
    private QuestionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Question() {}

    public Question(Long id, String text, List<OptionDTO> options, String correctOptionId, Long authorId, QuestionStatus status) {
        this.id = id;
        this.text = text;
        this.options = options;
        this.correctOptionId = correctOptionId;
        this.authorId = authorId;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public QuestionDTO toDTO() {
        return new QuestionDTO(id, text, options, correctOptionId, authorId, status, createdAt, updatedAt);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public List<OptionDTO> getOptions() { return options; }
    public void setOptions(List<OptionDTO> options) { this.options = options; }

    public String getCorrectOptionId() { return correctOptionId; }
    public void setCorrectOptionId(String correctOptionId) { this.correctOptionId = correctOptionId; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public QuestionStatus getStatus() { return status; }
    public void setStatus(QuestionStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
