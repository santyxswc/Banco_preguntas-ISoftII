package co.unicauca.iso2.taller09.reviewservice.domain.model;

import co.unicauca.iso2.taller09.common.enums.ReviewResult;
import co.unicauca.iso2.taller09.reviewservice.domain.exception.ReviewException;

import java.time.LocalDateTime;

public class Review {
    public static final String MENSAJE_RECHAZO_SIN_OBSERVACION =
            "Debe registrar al menos una observación para rechazar";

    private Long id;
    private final Long questionId;
    private final Long reviewerId;
    private final int round;
    private final ReviewResult result;
    private final String comments;
    private final LocalDateTime createdAt;

    public Review(Long id, Long questionId, Long reviewerId, int round, ReviewResult result,
                  String comments, LocalDateTime createdAt) {
        this.id = id;
        this.questionId = questionId;
        this.reviewerId = reviewerId;
        this.round = round;
        this.result = result;
        this.comments = comments;
        this.createdAt = createdAt;
    }

    public static Review register(Long questionId, Long reviewerId, int round, ReviewResult result, String comments) {
        if (result == null) {
            throw ReviewException.validation("La decisión (APPROVED o REJECTED) es obligatoria.");
        }
        String clean = comments == null ? null : comments.trim();
        if (result == ReviewResult.REJECTED && (clean == null || clean.isEmpty())) {
            throw ReviewException.validation(MENSAJE_RECHAZO_SIN_OBSERVACION);
        }
        return new Review(null, questionId, reviewerId, round, result,
                clean == null || clean.isEmpty() ? null : clean, LocalDateTime.now());
    }

    public boolean isApproved() {
        return result == ReviewResult.APPROVED;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getQuestionId() { return questionId; }
    public Long getReviewerId() { return reviewerId; }
    public int getRound() { return round; }
    public ReviewResult getResult() { return result; }
    public String getComments() { return comments; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
