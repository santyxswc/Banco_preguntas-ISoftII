/**
 * @file SpringQuestionRecordRepository.java
 * @brief Spring Data JPA interface for MultipleChoiceQuestionRecord.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.jpa.infrastructure.springdata;

import co.unicauca.iso2.bancopreguntas.jpa.infrastructure.record.MultipleChoiceQuestionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @brief Spring Data repository technology interface. Not visible to domain services.
 */
@Repository
public interface SpringQuestionRecordRepository extends JpaRepository<MultipleChoiceQuestionRecord, Long> {

    List<MultipleChoiceQuestionRecord> findByStatus(String status);

    List<MultipleChoiceQuestionRecord> findByAuthorId(String authorId);
}
