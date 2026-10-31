/**
 * @file CleanQuestion.java
 * @brief Pure domain question entity for Saber Pro question bank (Taller 10 - JPA).
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.jpa.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @brief Represents a Question in the domain layer.
 *
 * This class does NOT import jakarta.persistence nor contain any JPA annotations.
 * It contains core business attributes and domain rules.
 */
public class CleanQuestion {

    private Long id;
    private String authorId;
    private String stem;
    private String context;
    private String justification;
    private String competence;
    private String topic;
    private CleanQuestionStatus status = CleanQuestionStatus.DRAFT;
    private final List<CleanOption> options = new ArrayList<>();

    public CleanQuestion() {}

    public CleanQuestion(Long id, String authorId, String stem, String context, String justification, String competence, String topic) {
        this.id = id;
        this.authorId = authorId;
        this.stem = stem;
        this.context = context;
        this.justification = justification;
        this.competence = competence;
        this.topic = topic;
        this.status = CleanQuestionStatus.DRAFT;
    }

    /**
     * @brief Adds an option to the question.
     * @param option domain option to add.
     */
    public void addOption(CleanOption option) {
        if (this.options.size() >= 4) {
            throw new IllegalStateException("A question cannot have more than 4 options.");
        }
        this.options.add(option);
    }

    /**
     * @brief Submits the question for peer review if minimum requirements are met.
     */
    public void submitForReview() {
        if (this.options.size() != 4) {
            throw new IllegalStateException("Question must contain exactly 4 options to be submitted.");
        }
        long correctCount = options.stream().filter(CleanOption::isCorrect).count();
        if (correctCount != 1) {
            throw new IllegalStateException("Question must have exactly one correct answer.");
        }
        this.status = CleanQuestionStatus.PENDING_REVIEW;
    }

    public void approve() {
        this.status = CleanQuestionStatus.APPROVED;
    }

    public void reject() {
        this.status = CleanQuestionStatus.REJECTED;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getStem() {
        return stem;
    }

    public void setStem(String stem) {
        this.stem = stem;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public String getCompetence() {
        return competence;
    }

    public void setCompetence(String competence) {
        this.competence = competence;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public CleanQuestionStatus getStatus() {
        return status;
    }

    public void setStatus(CleanQuestionStatus status) {
        this.status = status;
    }

    public List<CleanOption> getOptions() {
        return Collections.unmodifiableList(options);
    }

    public void clearOptions() {
        this.options.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CleanQuestion that = (CleanQuestion) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
