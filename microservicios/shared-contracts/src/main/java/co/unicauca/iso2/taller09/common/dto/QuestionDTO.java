package co.unicauca.iso2.taller09.common.dto;

import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Vista de una pregunta que viaja entre servicios y hacia el frontend.
 * {@code statusColor} es el color hexadecimal con el que la interfaz pinta el estado (HU-02).
 */
public class QuestionDTO implements Serializable {
    private Long id;
    private String context;
    private String text;
    private List<OptionDTO> options;
    private String correctOptionId;
    private String justification;
    private String bibliography;
    private String competency;
    private String topic;
    private String subtopic;
    private String difficulty;
    private Long authorId;
    private QuestionStatus status;
    private String statusLabel;
    private String statusColor;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public QuestionDTO() {}

    public QuestionDTO(Long id, String text, List<OptionDTO> options, String correctOptionId, Long authorId, QuestionStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.text = text;
        this.options = options;
        this.correctOptionId = correctOptionId;
        this.authorId = authorId;
        setStatus(status);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public List<OptionDTO> getOptions() { return options; }
    public void setOptions(List<OptionDTO> options) { this.options = options; }

    public String getCorrectOptionId() { return correctOptionId; }
    public void setCorrectOptionId(String correctOptionId) { this.correctOptionId = correctOptionId; }

    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }

    public String getBibliography() { return bibliography; }
    public void setBibliography(String bibliography) { this.bibliography = bibliography; }

    public String getCompetency() { return competency; }
    public void setCompetency(String competency) { this.competency = competency; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getSubtopic() { return subtopic; }
    public void setSubtopic(String subtopic) { this.subtopic = subtopic; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public QuestionStatus getStatus() { return status; }
    public void setStatus(QuestionStatus status) {
        this.status = status;
        this.statusLabel = status == null ? null : status.label();
        this.statusColor = status == null ? null : status.color();
    }

    public String getStatusLabel() { return statusLabel; }
    public void setStatusLabel(String statusLabel) { this.statusLabel = statusLabel; }

    public String getStatusColor() { return statusColor; }
    public void setStatusColor(String statusColor) { this.statusColor = statusColor; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
