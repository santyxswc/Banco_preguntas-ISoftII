package co.unicauca.iso2.taller09.reviewservice.support;

import co.unicauca.iso2.taller09.reviewservice.domain.model.PageResult;
import co.unicauca.iso2.taller09.reviewservice.domain.model.Review;
import co.unicauca.iso2.taller09.reviewservice.domain.model.ReviewAssignment;
import co.unicauca.iso2.taller09.reviewservice.domain.port.ReviewRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryReviewRepository implements ReviewRepository {

    private final Map<Long, ReviewAssignment> assignments = new LinkedHashMap<>();
    private final Map<Long, Review> reviews = new LinkedHashMap<>();
    private final AtomicLong ids = new AtomicLong();

    @Override
    public ReviewAssignment saveAssignment(ReviewAssignment assignment) {
        if (assignment.getId() == null) {
            assignment.setId(ids.incrementAndGet());
        }
        assignments.put(assignment.getId(), assignment);
        return assignment;
    }

    @Override
    public List<ReviewAssignment> findAssignmentsByQuestion(Long questionId) {
        return assignments.values().stream().filter(a -> a.getQuestionId().equals(questionId)).toList();
    }

    @Override
    public Optional<ReviewAssignment> findLatestAssignment(Long questionId, Long reviewerId) {
        return assignments.values().stream()
                .filter(a -> a.getQuestionId().equals(questionId) && a.getReviewerId().equals(reviewerId))
                .max(Comparator.comparing(ReviewAssignment::getId));
    }

    @Override
    public PageResult<ReviewAssignment> findPendingByReviewer(Long reviewerId, int page, int size) {
        List<ReviewAssignment> pending = assignments.values().stream()
                .filter(a -> Objects.equals(a.getReviewerId(), reviewerId) && a.isPending())
                .sorted(Comparator.comparing(ReviewAssignment::getAssignedAt))
                .toList();
        int from = Math.min(page * size, pending.size());
        int to = Math.min(from + size, pending.size());
        return new PageResult<>(pending.subList(from, to), page, size, pending.size());
    }

    @Override
    public List<ReviewAssignment> findAllAssignments() {
        return new ArrayList<>(assignments.values());
    }

    @Override
    public Review saveReview(Review review) {
        if (review.getId() == null) {
            review.setId(ids.incrementAndGet());
        }
        reviews.put(review.getId(), review);
        return review;
    }

    @Override
    public List<Review> findReviewsByQuestion(Long questionId) {
        return reviews.values().stream().filter(r -> r.getQuestionId().equals(questionId)).toList();
    }
}
