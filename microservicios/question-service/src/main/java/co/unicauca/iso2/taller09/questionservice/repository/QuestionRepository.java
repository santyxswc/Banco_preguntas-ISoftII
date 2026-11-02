package co.unicauca.iso2.taller09.questionservice.repository;

import co.unicauca.iso2.taller09.questionservice.domain.Question;
import co.unicauca.iso2.taller09.questionservice.domain.QuestionFilter;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository {

    Question save(Question question);

    Optional<Question> findById(Long id);

    List<Question> findAll();

    List<Question> findByAuthorId(Long authorId);

    List<Question> findMatching(QuestionFilter filter);

    long count();
}
