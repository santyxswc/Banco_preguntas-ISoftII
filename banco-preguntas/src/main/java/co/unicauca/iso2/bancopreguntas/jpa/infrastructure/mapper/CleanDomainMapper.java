/**
 * @file CleanDomainMapper.java
 * @brief Bidirectional mapper between clean domain entities and JPA persistence records.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.jpa.infrastructure.mapper;

import co.unicauca.iso2.bancopreguntas.jpa.domain.model.CleanOption;
import co.unicauca.iso2.bancopreguntas.jpa.domain.model.CleanQuestion;
import co.unicauca.iso2.bancopreguntas.jpa.domain.model.CleanQuestionStatus;
import co.unicauca.iso2.bancopreguntas.jpa.domain.model.CleanQuiz;
import co.unicauca.iso2.bancopreguntas.jpa.infrastructure.record.MultipleChoiceQuestionRecord;
import co.unicauca.iso2.bancopreguntas.jpa.infrastructure.record.OptionRecord;
import co.unicauca.iso2.bancopreguntas.jpa.infrastructure.record.QuizRecord;

import java.util.stream.Collectors;

/**
 * @brief Isolates domain models from database records and schemas.
 */
public class CleanDomainMapper {

    /**
     * @brief Transforms a pure domain question into a JPA database record.
     * @param domain question in domain model.
     * @return multiple choice JPA record ready for persistence.
     */
    public MultipleChoiceQuestionRecord toRecord(CleanQuestion domain) {
        if (domain == null) return null;

        MultipleChoiceQuestionRecord record = new MultipleChoiceQuestionRecord();
        record.setId(domain.getId());
        record.setAuthorId(domain.getAuthorId());
        record.setStem(domain.getStem());
        record.setContext(domain.getContext());
        record.setJustification(domain.getJustification());
        record.setCompetence(domain.getCompetence());
        record.setTopic(domain.getTopic());
        record.setStatus(domain.getStatus() != null ? domain.getStatus().name() : CleanQuestionStatus.DRAFT.name());

        if (domain.getOptions() != null) {
            for (CleanOption opt : domain.getOptions()) {
                OptionRecord optRec = toRecord(opt);
                record.addOption(optRec);
            }
        }
        return record;
    }

    /**
     * @brief Transforms a JPA record back into a pure domain question entity.
     * @param record JPA entity fetched from the database.
     * @return clean domain question.
     */
    public CleanQuestion toDomain(MultipleChoiceQuestionRecord record) {
        if (record == null) return null;

        CleanQuestion domain = new CleanQuestion();
        domain.setId(record.getId());
        domain.setAuthorId(record.getAuthorId());
        domain.setStem(record.getStem());
        domain.setContext(record.getContext());
        domain.setJustification(record.getJustification());
        domain.setCompetence(record.getCompetence());
        domain.setTopic(record.getTopic());
        if (record.getStatus() != null) {
            domain.setStatus(CleanQuestionStatus.valueOf(record.getStatus()));
        }

        if (record.getOptions() != null) {
            for (OptionRecord optRec : record.getOptions()) {
                domain.addOption(toDomain(optRec));
            }
        }
        return domain;
    }

    public OptionRecord toRecord(CleanOption domain) {
        if (domain == null) return null;
        OptionRecord record = new OptionRecord();
        record.setId(domain.getId());
        record.setText(domain.getText());
        record.setCorrect(domain.isCorrect());
        return record;
    }

    public CleanOption toDomain(OptionRecord record) {
        if (record == null) return null;
        return new CleanOption(record.getId(), record.getText(), record.isCorrect());
    }

    public QuizRecord toRecord(CleanQuiz domain) {
        if (domain == null) return null;
        QuizRecord record = new QuizRecord();
        record.setId(domain.getId());
        record.setTitle(domain.getTitle());
        record.setModule(domain.getModule());
        if (domain.getQuestions() != null) {
            record.setQuestions(domain.getQuestions().stream()
                    .map(this::toRecord)
                    .collect(Collectors.toList()));
        }
        return record;
    }

    public CleanQuiz toDomain(QuizRecord record) {
        if (record == null) return null;
        CleanQuiz domain = new CleanQuiz(record.getId(), record.getTitle(), record.getModule());
        if (record.getQuestions() != null) {
            for (MultipleChoiceQuestionRecord qRec : record.getQuestions()) {
                CleanQuestion qDomain = toDomain(qRec);
                if (qDomain != null) {
                    domain.addQuestion(qDomain);
                }
            }
        }
        return domain;
    }
}
