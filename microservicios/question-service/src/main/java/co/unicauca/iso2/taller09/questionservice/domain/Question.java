package co.unicauca.iso2.taller09.questionservice.domain;

import co.unicauca.iso2.taller09.common.dto.OptionDTO;
import co.unicauca.iso2.taller09.common.dto.QuestionDTO;
import co.unicauca.iso2.taller09.common.enums.QuestionStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

/**
 * Pregunta de selección múltiple con única respuesta (contexto de Autoría, HU-01).
 * <p>
 * Se construye con {@link QuestionBuilder}. El estado que guarda es solo una copia local: la
 * autoridad del estado es lifecycle-service, por eso al listar se reemplaza con el que él reporta.
 */
public class Question {
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    Question() {
        this.status = QuestionStatus.DRAFT;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    /** Constructor histórico del Taller 09; se conserva por compatibilidad. */
    public Question(Long id, String text, List<OptionDTO> options, String correctOptionId, Long authorId, QuestionStatus status) {
        this();
        this.id = id;
        this.text = text;
        this.options = options;
        this.correctOptionId = correctOptionId;
        this.authorId = authorId;
        this.status = status;
    }

    /** Reemplaza el contenido editable con el de otra versión de la pregunta (edición en borrador). */
    public void updateContentFrom(Question other) {
        this.context = other.context;
        this.text = other.text;
        this.options = other.options;
        this.correctOptionId = other.correctOptionId;
        this.justification = other.justification;
        this.bibliography = other.bibliography;
        this.competency = other.competency;
        this.topic = other.topic;
        this.subtopic = other.subtopic;
        this.difficulty = other.difficulty;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isAuthoredBy(Long userId) {
        return userId != null && userId.equals(authorId);
    }

    /** Busca la palabra clave en el contexto, el enunciado, el tema y el subtema, sin distinguir mayúsculas. */
    public boolean mentions(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }
        String k = keyword.toLowerCase(Locale.ROOT);
        return contains(context, k) || contains(text, k) || contains(topic, k) || contains(subtopic, k);
    }

    private static boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keyword);
    }

    public QuestionDTO toDTO() {
        QuestionDTO dto = new QuestionDTO(id, text, options, correctOptionId, authorId, status, createdAt, updatedAt);
        dto.setContext(context);
        dto.setJustification(justification);
        dto.setBibliography(bibliography);
        dto.setCompetency(competency);
        dto.setTopic(topic);
        dto.setSubtopic(subtopic);
        dto.setDifficulty(difficulty);
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContext() { return context; }
    void setContext(String context) { this.context = context; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public List<OptionDTO> getOptions() { return options; }
    public void setOptions(List<OptionDTO> options) { this.options = options; }

    public String getCorrectOptionId() { return correctOptionId; }
    public void setCorrectOptionId(String correctOptionId) { this.correctOptionId = correctOptionId; }

    public String getJustification() { return justification; }
    void setJustification(String justification) { this.justification = justification; }

    public String getBibliography() { return bibliography; }
    void setBibliography(String bibliography) { this.bibliography = bibliography; }

    public String getCompetency() { return competency; }
    void setCompetency(String competency) { this.competency = competency; }

    public String getTopic() { return topic; }
    void setTopic(String topic) { this.topic = topic; }

    public String getSubtopic() { return subtopic; }
    void setSubtopic(String subtopic) { this.subtopic = subtopic; }

    public String getDifficulty() { return difficulty; }
    void setDifficulty(String difficulty) { this.difficulty = difficulty; }

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
