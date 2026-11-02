package co.unicauca.iso2.taller09.questionservice.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface QuestionJpaRepository extends JpaRepository<QuestionEntity, Long>, JpaSpecificationExecutor<QuestionEntity> {

    List<QuestionEntity> findByAuthorIdOrderByIdDesc(Long authorId);
}
