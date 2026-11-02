package co.unicauca.iso2.taller09.reviewservice.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewJpaRepository extends JpaRepository<ReviewEntity, Long> {

    List<ReviewEntity> findByQuestionIdOrderByCreatedAtAscIdAsc(Long questionId);
}
