/**
 * @file QuizRecord.java
 * @brief JPA record representing an examination quiz with ManyToMany question associations.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.jpa.infrastructure.record;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * @brief Entity representing a quiz/exam in table 'jpa_quizzes'.
 *
 * Uses the join table 'jpa_quiz_questions' to hold the many-to-many relationship with questions.
 */
@Entity
@Table(name = "jpa_quizzes")
public class QuizRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "module_name", length = 100)
    private String module;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "jpa_quiz_questions",
            joinColumns = @JoinColumn(name = "quiz_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    private List<MultipleChoiceQuestionRecord> questions = new ArrayList<>();

    public QuizRecord() {}

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

    public List<MultipleChoiceQuestionRecord> getQuestions() {
        return questions;
    }

    public void setQuestions(List<MultipleChoiceQuestionRecord> questions) {
        this.questions = questions;
    }
}
