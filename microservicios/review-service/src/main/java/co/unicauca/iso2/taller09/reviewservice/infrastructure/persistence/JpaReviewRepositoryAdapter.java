package co.unicauca.iso2.taller09.reviewservice.infrastructure.persistence;

import co.unicauca.iso2.taller09.common.enums.AssignmentStatus;
import co.unicauca.iso2.taller09.reviewservice.domain.model.PageResult;
import co.unicauca.iso2.taller09.reviewservice.domain.model.Review;
import co.unicauca.iso2.taller09.reviewservice.domain.model.ReviewAssignment;
import co.unicauca.iso2.taller09.reviewservice.domain.port.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class JpaReviewRepositoryAdapter implements ReviewRepository {

    private final AssignmentJpaRepository assignments;
    private final ReviewJpaRepository reviews;

    public JpaReviewRepositoryAdapter(AssignmentJpaRepository assignments, ReviewJpaRepository reviews) {
        this.assignments = assignments;
        this.reviews = reviews;
    }

    @Override
    public ReviewAssignment saveAssignment(ReviewAssignment assignment) {
        AssignmentEntity saved = assignments.save(toEntity(assignment));
        assignment.setId(saved.getId());
        return assignment;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewAssignment> findAssignmentsByQuestion(Long questionId) {
        return assignments.findByQuestionIdOrderByIdAsc(questionId).stream().map(this::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReviewAssignment> findLatestAssignment(Long questionId, Long reviewerId) {
        return assignments.findFirstByQuestionIdAndReviewerIdOrderByIdDesc(questionId, reviewerId).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<ReviewAssignment> findPendingByReviewer(Long reviewerId, int page, int size) {
        Page<AssignmentEntity> result = assignments.findByReviewerIdAndStatus(reviewerId, AssignmentStatus.ASSIGNED,
                PageRequest.of(page, size, Sort.by("assignedAt").ascending()));
        return new PageResult<>(result.getContent().stream().map(this::toDomain).toList(),
                page, size, result.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewAssignment> findAllAssignments() {
        return assignments.findAll(Sort.by("id")).stream().map(this::toDomain).toList();
    }

    @Override
    public Review saveReview(Review review) {
        ReviewEntity saved = reviews.save(new ReviewEntity(review.getId(), review.getQuestionId(),
                review.getReviewerId(), review.getRound(), review.getResult(), review.getComments(), review.getCreatedAt()));
        review.setId(saved.getId());
        return review;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> findReviewsByQuestion(Long questionId) {
        return reviews.findByQuestionIdOrderByCreatedAtAscIdAsc(questionId).stream()
                .map(e -> new Review(e.getId(), e.getQuestionId(), e.getReviewerId(), e.getRound(), e.getResult(),
                        e.getComments(), e.getCreatedAt()))
                .toList();
    }

    private AssignmentEntity toEntity(ReviewAssignment a) {
        return new AssignmentEntity(a.getId(), a.getQuestionId(), a.getReviewerId(), a.getAssignedBy(),
                a.getRound(), a.getAssignedAt(), a.getStatus());
    }

    private ReviewAssignment toDomain(AssignmentEntity e) {
        return new ReviewAssignment(e.getId(), e.getQuestionId(), e.getReviewerId(), e.getAssignedBy(),
                e.getRound(), e.getAssignedAt(), e.getStatus());
    }
}
