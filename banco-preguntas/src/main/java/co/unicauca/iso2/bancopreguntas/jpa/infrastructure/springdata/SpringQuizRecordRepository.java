/**
 * @file SpringQuizRecordRepository.java
 * @brief Spring Data JPA interface for QuizRecord.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.jpa.infrastructure.springdata;

import co.unicauca.iso2.bancopreguntas.jpa.infrastructure.record.QuizRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @brief Spring Data repository technology interface for Quizzes.
 */
@Repository
public interface SpringQuizRecordRepository extends JpaRepository<QuizRecord, Long> {
}
