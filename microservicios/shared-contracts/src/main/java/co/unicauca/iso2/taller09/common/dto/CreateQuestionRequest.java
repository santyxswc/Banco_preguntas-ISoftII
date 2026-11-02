package co.unicauca.iso2.taller09.common.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Solicitud para crear o editar una pregunta Saber Pro (HU-01).
 * <p>
 * Incluye todos los elementos del Diseño Centrado en Evidencia: contexto, enunciado
 * (pregunta directa), cuatro opciones, respuesta correcta, justificación, bibliografía
 * y la taxonomía (competencia, tema, subtema y nivel de dificultad).
 */
public class CreateQuestionRequest implements Serializable {
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

    public CreateQuestionRequest() {}

    public CreateQuestionRequest(String text, List<OptionDTO> options, String correctOptionId, Long authorId) {
        this.text = text;
        this.options = options;
        this.correctOptionId = correctOptionId;
        this.authorId = authorId;
    }

    public CreateQuestionRequest(String context, String text, List<OptionDTO> options, String correctOptionId,
                                 String justification, String bibliography, String competency, String topic,
                                 String subtopic, String difficulty, Long authorId) {
        this(text, options, correctOptionId, authorId);
        this.context = context;
        this.justification = justification;
        this.bibliography = bibliography;
        this.competency = competency;
        this.topic = topic;
        this.subtopic = subtopic;
        this.difficulty = difficulty;
    }

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
}
