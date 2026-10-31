/**
 * @file CleanQuizRepository.java
 * @brief Abstract technology-agnostic repository contract for clean quizzes.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.jpa.domain.repository;

import co.unicauca.iso2.bancopreguntas.jpa.domain.model.CleanQuiz;

import java.util.List;
import java.util.Optional;

/**
 * @brief Repository contract for managing quizzes independently of persistence technology.
 */
public interface CleanQuizRepository {

    CleanQuiz save(CleanQuiz quiz);

    Optional<CleanQuiz> findById(Long id);

    List<CleanQuiz> findAll();

    void deleteById(Long id);
}
