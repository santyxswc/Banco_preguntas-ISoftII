/**
 * @file CleanQuestionRepository.java
 * @brief Abstract technology-agnostic repository contract for clean domain questions.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.jpa.domain.repository;

import co.unicauca.iso2.bancopreguntas.jpa.domain.model.CleanQuestion;
import co.unicauca.iso2.bancopreguntas.jpa.domain.model.CleanQuestionStatus;

import java.util.List;
import java.util.Optional;

/**
 * @brief Repository contract expressed strictly in domain language.
 */
public interface CleanQuestionRepository {

    CleanQuestion save(CleanQuestion question);

    Optional<CleanQuestion> findById(Long id);

    List<CleanQuestion> findAll();

    List<CleanQuestion> findByStatus(CleanQuestionStatus status);

    List<CleanQuestion> findByAuthorId(String authorId);

    void deleteById(Long id);
}
