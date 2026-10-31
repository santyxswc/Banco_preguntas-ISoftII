/**
 * @file CleanQuizRepositoryJpaAdapter.java
 * @brief Concrete JPA implementation of the domain CleanQuizRepository.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.jpa.infrastructure.adapter;

import co.unicauca.iso2.bancopreguntas.jpa.domain.model.CleanQuiz;
import co.unicauca.iso2.bancopreguntas.jpa.domain.repository.CleanQuizRepository;
import co.unicauca.iso2.bancopreguntas.jpa.infrastructure.mapper.CleanDomainMapper;
import co.unicauca.iso2.bancopreguntas.jpa.infrastructure.record.QuizRecord;
import co.unicauca.iso2.bancopreguntas.jpa.infrastructure.springdata.SpringQuizRecordRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @brief Concrete adapter for quiz persistence using Spring Data JPA.
 */
public class CleanQuizRepositoryJpaAdapter implements CleanQuizRepository {

    private final SpringQuizRecordRepository springDataQuizRepo;
    private final CleanDomainMapper mapper;

    public CleanQuizRepositoryJpaAdapter(SpringQuizRecordRepository springDataQuizRepo, CleanDomainMapper mapper) {
        this.springDataQuizRepo = springDataQuizRepo;
        this.mapper = mapper;
    }

    @Override
    public CleanQuiz save(CleanQuiz quiz) {
        QuizRecord record = mapper.toRecord(quiz);
        QuizRecord saved = springDataQuizRepo.save(record);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CleanQuiz> findById(Long id) {
        return springDataQuizRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CleanQuiz> findAll() {
        return springDataQuizRepo.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        springDataQuizRepo.deleteById(id);
    }
}
