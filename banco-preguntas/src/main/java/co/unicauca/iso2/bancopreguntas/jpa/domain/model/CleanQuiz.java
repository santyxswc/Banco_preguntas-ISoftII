/**
 * @file CleanQuiz.java
 * @brief Domain entity representing a Quiz or Examination made of approved questions (Taller 10 - JPA).
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
 * @brief Represents a Quiz in the clean domain layer.
 *
 * Enforces business rule: Only APPROVED / PUBLISHED questions can be added to an active quiz.
 */
public class CleanQuiz {

    private Long id;
    private String title;
    private String module;
    private final List<CleanQuestion> questions = new ArrayList<>();

    public CleanQuiz() {}

    public CleanQuiz(Long id, String title, String module) {
        this.id = id;
        this.title = title;
        this.module = module;
    }

    /**
     * @brief Adds a question to the quiz enforcing lifecycle integrity.
     * @param question question to attach.
     * @throws IllegalStateException if question is not in APPROVED status.
     */
    public void addQuestion(CleanQuestion question) {
        Objects.requireNonNull(question, "Question cannot be null");
        if (question.getStatus() != CleanQuestionStatus.APPROVED) {
            throw new IllegalStateException("Cannot add question " + question.getId()
                    + " to quiz because it is not APPROVED (current status: "
                    + question.getStatus() + ").");
        }
        if (!questions.contains(question)) {
            questions.add(question);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public List<CleanQuestion> getQuestions() {
        return Collections.unmodifiableList(questions);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CleanQuiz cleanQuiz = (CleanQuiz) o;
        return Objects.equals(id, cleanQuiz.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
