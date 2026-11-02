package co.unicauca.iso2.taller09.questionservice.domain;

import co.unicauca.iso2.taller09.common.dto.CreateQuestionRequest;
import co.unicauca.iso2.taller09.common.dto.OptionDTO;
import co.unicauca.iso2.taller09.common.enums.QuestionStatus;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Patrón GoF <b>Builder</b> (creacional).
 * <p>
 * Una pregunta Saber Pro tiene once elementos (contexto, enunciado, opciones, clave,
 * justificación, bibliografía, competencia, tema, subtema, dificultad y autor). El builder
 * evita un constructor con once parámetros, permite armarla paso a paso desde la solicitud
 * REST o desde los datos de demostración, y centraliza la normalización (recortar textos,
 * dificultad en mayúsculas) y las invariantes mínimas antes de crear el objeto.
 */
public final class QuestionBuilder {

    private final Question question = new Question();

    private QuestionBuilder() {
    }

    public static QuestionBuilder aQuestion() {
        return new QuestionBuilder();
    }

    /** Copia todos los campos de contenido de la solicitud REST. */
    public static QuestionBuilder from(CreateQuestionRequest request) {
        return aQuestion()
                .context(request.getContext())
                .statement(request.getText())
                .options(request.getOptions())
                .correctOption(request.getCorrectOptionId())
                .justification(request.getJustification())
                .bibliography(request.getBibliography())
                .competency(request.getCompetency())
                .topic(request.getTopic())
                .subtopic(request.getSubtopic())
                .difficulty(request.getDifficulty())
                .author(request.getAuthorId());
    }

    public QuestionBuilder context(String context) {
        question.setContext(trim(context));
        return this;
    }

    public QuestionBuilder statement(String text) {
        question.setText(trim(text));
        return this;
    }

    public QuestionBuilder options(List<OptionDTO> options) {
        question.setOptions(options == null ? List.of() : List.copyOf(options));
        return this;
    }

    public QuestionBuilder option(String id, String text) {
        List<OptionDTO> current = question.getOptions() == null ? List.of() : question.getOptions();
        java.util.ArrayList<OptionDTO> next = new java.util.ArrayList<>(current);
        next.add(new OptionDTO(id, text));
        question.setOptions(List.copyOf(next));
        return this;
    }

    public QuestionBuilder correctOption(String optionId) {
        question.setCorrectOptionId(trim(optionId));
        return this;
    }

    public QuestionBuilder justification(String justification) {
        question.setJustification(trim(justification));
        return this;
    }

    public QuestionBuilder bibliography(String bibliography) {
        question.setBibliography(trim(bibliography));
        return this;
    }

    public QuestionBuilder competency(String competency) {
        question.setCompetency(trim(competency));
        return this;
    }

    public QuestionBuilder topic(String topic) {
        question.setTopic(trim(topic));
        return this;
    }

    public QuestionBuilder subtopic(String subtopic) {
        question.setSubtopic(trim(subtopic));
        return this;
    }

    public QuestionBuilder difficulty(String difficulty) {
        String value = trim(difficulty);
        question.setDifficulty(value == null ? null : value.toUpperCase(Locale.ROOT));
        return this;
    }

    public QuestionBuilder author(Long authorId) {
        question.setAuthorId(authorId);
        return this;
    }

    public QuestionBuilder status(QuestionStatus status) {
        question.setStatus(status);
        return this;
    }

    /**
     * Crea la pregunta. Las reglas pedagógicas completas las aplica validation-service; aquí
     * solo se garantizan las invariantes sin las cuales el objeto no tiene sentido.
     */
    public Question build() {
        Objects.requireNonNull(question.getAuthorId(), "La pregunta debe tener un autor");
        if (question.getText() == null || question.getText().isBlank()) {
            throw new IllegalStateException("La pregunta debe tener un enunciado");
        }
        if (question.getOptions() == null) {
            question.setOptions(List.of());
        }
        return question;
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
