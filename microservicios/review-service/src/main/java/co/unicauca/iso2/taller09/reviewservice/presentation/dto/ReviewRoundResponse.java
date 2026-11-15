package co.unicauca.iso2.taller09.reviewservice.presentation.dto;

import co.unicauca.iso2.taller09.reviewservice.domain.model.ReviewRound;
import co.unicauca.iso2.taller09.reviewservice.domain.model.RoundOutcome;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Una ronda del historial de revisiones (HU-06) tal como la recibe el frontend: resultado con
 * su etiqueta y color, revisores asignados, revisores pendientes y las evaluaciones.
 */
public record ReviewRoundResponse(int round, RoundOutcome outcome, String outcomeLabel, String outcomeColor,
                                  LocalDateTime startedAt, LocalDateTime closedAt, List<Long> reviewerIds,
                                  List<Long> pendingReviewerIds, List<ReviewResponse> reviews) {

    public static ReviewRoundResponse from(ReviewRound round) {
        return new ReviewRoundResponse(round.number(), round.outcome(), round.outcome().label(),
                StateColors.of(round.outcome()), round.startedAt(), round.closedAt(), round.reviewerIds(),
                round.pendingReviewerIds(), round.reviews().stream().map(ReviewResponse::from).toList());
    }
}
