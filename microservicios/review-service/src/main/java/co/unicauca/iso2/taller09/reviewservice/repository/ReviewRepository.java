package co.unicauca.iso2.taller09.reviewservice.repository;

import co.unicauca.iso2.taller09.reviewservice.domain.Review;
import co.unicauca.iso2.taller09.reviewservice.domain.ReviewAssignment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ReviewRepository {
    private final Map<Long, ReviewAssignment> assignments = new ConcurrentHashMap<>();
    private final Map<Long, Review> reviews = new ConcurrentHashMap<>();
    private final AtomicLong assignmentIdGen = new AtomicLong(1);
    private final AtomicLong reviewIdGen = new AtomicLong(1);

    public ReviewAssignment saveAssignment(ReviewAssignment assignment) {
        if (assignment.getId() == null) {
            assignment.setId(assignmentIdGen.incrementAndGet());
        }
        assignments.put(assignment.getId(), assignment);
        return assignment;
    }

    public List<ReviewAssignment> findAssignmentsByQuestionId(Long questionId) {
        return assignments.values().stream()
                .filter(a -> a.getQuestionId().equals(questionId))
                .toList();
    }

    public List<ReviewAssignment> findAllAssignments() {
        return new ArrayList<>(assignments.values());
    }

    public Review saveReview(Review review) {
        if (review.getId() == null) {
            review.setId(reviewIdGen.incrementAndGet());
        }
        reviews.put(review.getId(), review);
        return review;
    }

    public List<Review> findReviewsByQuestionId(Long questionId) {
        return reviews.values().stream()
                .filter(r -> r.getQuestionId().equals(questionId))
                .toList();
    }

    public List<Review> findAllReviews() {
        return new ArrayList<>(reviews.values());
    }
}
