package co.unicauca.iso2.taller09.lifecycleservice.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** Repositorio Spring Data de los estados de las preguntas. */
public interface QuestionStateJpaRepository extends JpaRepository<QuestionStateEntity, Long> {

    Optional<QuestionStateEntity> findByQuestionId(Long questionId);
}
