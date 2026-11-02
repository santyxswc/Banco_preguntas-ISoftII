package co.unicauca.iso2.taller09.reviewservice.domain.policy;

import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import co.unicauca.iso2.taller09.reviewservice.domain.exception.ReviewException;
import co.unicauca.iso2.taller09.reviewservice.domain.model.Review;

import java.util.List;

public class UnanimousApprovalPolicy implements VerdictPolicy {

    @Override
    public QuestionStatus decide(List<Review> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            throw ReviewException.validation("No hay evaluaciones para decidir el estado de la pregunta.");
        }
        return reviews.stream().allMatch(Review::isApproved)
                ? QuestionStatus.APPROVED
                : QuestionStatus.REJECTED;
    }
}
