package co.unicauca.iso2.taller09.questionservice.repository.jpa;

import co.unicauca.iso2.taller09.common.dto.OptionDTO;
import co.unicauca.iso2.taller09.questionservice.domain.Question;
import co.unicauca.iso2.taller09.questionservice.domain.QuestionBuilder;
import co.unicauca.iso2.taller09.questionservice.domain.QuestionFilter;
import co.unicauca.iso2.taller09.questionservice.repository.QuestionRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class JpaQuestionRepository implements QuestionRepository {

    private static final Sort NEWEST_FIRST = Sort.by(Sort.Direction.DESC, "id");

    private final QuestionJpaRepository jpa;

    public JpaQuestionRepository(QuestionJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Question save(Question question) {
        QuestionEntity entity = question.getId() == null
                ? QuestionEntity.create()
                : jpa.findById(question.getId()).orElseGet(QuestionEntity::create);
        copy(question, entity);
        QuestionEntity saved = jpa.save(entity);
        question.setId(saved.getId());
        return question;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Question> findById(Long id) {
        return jpa.findById(id).map(JpaQuestionRepository::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Question> findAll() {
        return jpa.findAll(NEWEST_FIRST).stream().map(JpaQuestionRepository::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Question> findByAuthorId(Long authorId) {
        return jpa.findByAuthorIdOrderByIdDesc(authorId).stream().map(JpaQuestionRepository::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Question> findMatching(QuestionFilter filter) {
        return jpa.findAll(QuestionSpecifications.matching(filter), NEWEST_FIRST).stream()
                .map(JpaQuestionRepository::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return jpa.count();
    }

    private static void copy(Question source, QuestionEntity target) {
        target.setContext(source.getContext());
        target.setStatement(source.getText());
        target.getOptions().clear();
        if (source.getOptions() != null) {
            source.getOptions().forEach(o -> target.getOptions().add(new OptionValue(o.getId(), o.getText())));
        }
        target.setCorrectOptionId(source.getCorrectOptionId());
        target.setJustification(source.getJustification());
        target.setBibliography(source.getBibliography());
        target.setCompetency(source.getCompetency());
        target.setTopic(source.getTopic());
        target.setSubtopic(source.getSubtopic());
        target.setDifficulty(source.getDifficulty());
        target.setAuthorId(source.getAuthorId());
        target.setStatus(source.getStatus());
        target.setCreatedAt(source.getCreatedAt());
        target.setUpdatedAt(source.getUpdatedAt());
    }

    static Question toDomain(QuestionEntity e) {
        Question question = QuestionBuilder.aQuestion()
                .context(e.getContext())
                .statement(e.getStatement())
                .options(e.getOptions().stream().map(o -> new OptionDTO(o.getOptionId(), o.getText())).toList())
                .correctOption(e.getCorrectOptionId())
                .justification(e.getJustification())
                .bibliography(e.getBibliography())
                .competency(e.getCompetency())
                .topic(e.getTopic())
                .subtopic(e.getSubtopic())
                .difficulty(e.getDifficulty())
                .author(e.getAuthorId())
                .status(e.getStatus())
                .build();
        question.setId(e.getId());
        question.setCreatedAt(e.getCreatedAt());
        question.setUpdatedAt(e.getUpdatedAt());
        return question;
    }
}
