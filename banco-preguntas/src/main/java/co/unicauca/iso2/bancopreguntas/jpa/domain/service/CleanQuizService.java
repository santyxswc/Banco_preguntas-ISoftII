/**
 * @file CleanQuizService.java
 * @brief Application / domain service orchestrating the required use cases for Taller 10 JPA.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.jpa.domain.service;

import co.unicauca.iso2.bancopreguntas.jpa.domain.model.CleanOption;
import co.unicauca.iso2.bancopreguntas.jpa.domain.model.CleanQuestion;
import co.unicauca.iso2.bancopreguntas.jpa.domain.model.CleanQuestionStatus;
import co.unicauca.iso2.bancopreguntas.jpa.domain.model.CleanQuiz;
import co.unicauca.iso2.bancopreguntas.jpa.domain.repository.CleanQuestionRepository;
import co.unicauca.iso2.bancopreguntas.jpa.domain.repository.CleanQuizRepository;

import java.util.List;

/**
 * @brief Coordinates use cases 1 to 5 required in Taller 10.
 *
 * Notice this service depends strictly on CleanQuestionRepository and CleanQuizRepository
 * domain interfaces, NEVER on Spring Data JPA or EntityManager directly.
 */
public class CleanQuizService {

    private final CleanQuestionRepository questionRepository;
    private final CleanQuizRepository quizRepository;

    public CleanQuizService(CleanQuestionRepository questionRepository, CleanQuizRepository quizRepository) {
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
    }

    /**
     * @brief Use Case 1: Create a question with author, stem, context, options.
     */
    public CleanQuestion createQuestion(String authorId, String stem, String context, String justification,
                                        String competence, String topic, List<CleanOption> options) {
        CleanQuestion q = new CleanQuestion(null, authorId, stem, context, justification, competence, topic);
        if (options != null) {
            options.forEach(q::addOption);
        }
        return questionRepository.save(q);
    }

    /**
     * @brief Use Case 2: Query questions by id and criteria.
     */
    public CleanQuestion getQuestion(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Question not found with id: " + id));
    }

    public List<CleanQuestion> getAllQuestions() {
        return questionRepository.findAll();
    }

    /**
     * @brief Use Case 3: Classify a question (competence and topic).
     */
    public CleanQuestion classifyQuestion(Long id, String competence, String topic) {
        CleanQuestion q = getQuestion(id);
        q.setCompetence(competence);
        q.setTopic(topic);
        return questionRepository.save(q);
    }

    /**
     * @brief Use Case 4: Submit question for peer review.
     */
    public CleanQuestion submitForReview(Long id) {
        CleanQuestion q = getQuestion(id);
        q.submitForReview();
        return questionRepository.save(q);
    }

    /**
     * @brief Approve question for publication.
     */
    public CleanQuestion approveQuestion(Long id) {
        CleanQuestion q = getQuestion(id);
        q.approve();
        return questionRepository.save(q);
    }

    /**
     * @brief Use Case 5: Create a quiz associating only APPROVED questions.
     */
    public CleanQuiz createQuiz(String title, String module, List<Long> questionIds) {
        CleanQuiz quiz = new CleanQuiz(null, title, module);
        for (Long qId : questionIds) {
            CleanQuestion question = getQuestion(qId);
            // Business rule: quiz.addQuestion throws exception if question is not APPROVED
            quiz.addQuestion(question);
        }
        return quizRepository.save(quiz);
    }
}
