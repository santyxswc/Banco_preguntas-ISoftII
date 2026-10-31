/**
 * @file CleanQuestionRepositoryJpaAdapter.java
 * @brief Concrete JPA implementation of the domain CleanQuestionRepository.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.jpa.infrastructure.adapter;

import co.unicauca.iso2.bancopreguntas.jpa.domain.model.CleanQuestion;
import co.unicauca.iso2.bancopreguntas.jpa.domain.model.CleanQuestionStatus;
import co.unicauca.iso2.bancopreguntas.jpa.domain.repository.CleanQuestionRepository;
import co.unicauca.iso2.bancopreguntas.jpa.infrastructure.mapper.CleanDomainMapper;
import co.unicauca.iso2.bancopreguntas.jpa.infrastructure.record.MultipleChoiceQuestionRecord;
import co.unicauca.iso2.bancopreguntas.jpa.infrastructure.springdata.SpringQuestionRecordRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @brief Adapts the domain repository contract to Spring Data JPA and Hibernate.
 */
public class CleanQuestionRepositoryJpaAdapter implements CleanQuestionRepository {

    private final SpringQuestionRecordRepository springDataRepo;
    private final CleanDomainMapper mapper;

    public CleanQuestionRepositoryJpaAdapter(SpringQuestionRecordRepository springDataRepo, CleanDomainMapper mapper) {
        this.springDataRepo = springDataRepo;
        this.mapper = mapper;
    }

    @Override
    public CleanQuestion save(CleanQuestion question) {
        MultipleChoiceQuestionRecord record = mapper.toRecord(question);
        MultipleChoiceQuestionRecord saved = springDataRepo.save(record);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CleanQuestion> findById(Long id) {
        return springDataRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CleanQuestion> findAll() {
        return springDataRepo.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CleanQuestion> findByStatus(CleanQuestionStatus status) {
        return springDataRepo.findByStatus(status.name()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CleanQuestion> findByAuthorId(String authorId) {
        return springDataRepo.findByAuthorId(authorId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        springDataRepo.deleteById(id);
    }
}
