package co.unicauca.iso2.taller09.reviewservice.infrastructure.persistence;

import co.unicauca.iso2.taller09.common.enums.AssignmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssignmentJpaRepository extends JpaRepository<AssignmentEntity, Long> {

    List<AssignmentEntity> findByQuestionIdOrderByIdAsc(Long questionId);

    Optional<AssignmentEntity> findFirstByQuestionIdAndReviewerIdOrderByIdDesc(Long questionId, Long reviewerId);

    Page<AssignmentEntity> findByReviewerIdAndStatus(Long reviewerId, AssignmentStatus status, Pageable pageable);
}
