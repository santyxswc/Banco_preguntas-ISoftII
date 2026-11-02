package co.unicauca.iso2.taller09.reviewservice.domain.port;

import co.unicauca.iso2.taller09.reviewservice.domain.model.PageResult;
import co.unicauca.iso2.taller09.reviewservice.domain.model.Review;
import co.unicauca.iso2.taller09.reviewservice.domain.model.ReviewAssignment;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {

    ReviewAssignment saveAssignment(ReviewAssignment assignment);

    List<ReviewAssignment> findAssignmentsByQuestion(Long questionId);

    Optional<ReviewAssignment> findLatestAssignment(Long questionId, Long reviewerId);

    PageResult<ReviewAssignment> findPendingByReviewer(Long reviewerId, int page, int size);

    List<ReviewAssignment> findAllAssignments();

    Review saveReview(Review review);

    List<Review> findReviewsByQuestion(Long questionId);
}
