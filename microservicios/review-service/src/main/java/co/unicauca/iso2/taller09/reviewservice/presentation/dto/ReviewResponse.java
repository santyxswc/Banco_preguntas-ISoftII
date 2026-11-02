package co.unicauca.iso2.taller09.reviewservice.presentation.dto;

import co.unicauca.iso2.taller09.common.enums.ReviewResult;
import co.unicauca.iso2.taller09.reviewservice.domain.model.Review;

import java.time.LocalDateTime;

public record ReviewResponse(Long id, Long questionId, Long reviewerId, int round, ReviewResult result,
                             String comments, LocalDateTime createdAt) {

    public static ReviewResponse from(Review r) {
        return new ReviewResponse(r.getId(), r.getQuestionId(), r.getReviewerId(), r.getRound(), r.getResult(),
                r.getComments(), r.getCreatedAt());
    }
}
