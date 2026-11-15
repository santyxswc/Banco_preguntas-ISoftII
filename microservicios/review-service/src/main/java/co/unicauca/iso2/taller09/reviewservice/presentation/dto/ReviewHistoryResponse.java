package co.unicauca.iso2.taller09.reviewservice.presentation.dto;

import co.unicauca.iso2.taller09.reviewservice.application.QuestionReviewHistory;
import co.unicauca.iso2.taller09.reviewservice.domain.model.ReviewHistory;

import java.util.List;

/**
 * Respuesta de {@code GET /api/v1/revisiones/pregunta/{id}/historial} (HU-06).
 * <p>
 * {@code improvedVersionAllowed} le indica al frontend si debe ofrecer el botón "Crear versión
 * mejorada" y {@code observationsToAddress} resume lo que el autor debe corregir.
 */
public record ReviewHistoryResponse(Long questionId, String questionText, Long authorId, int totalRounds,
                                    boolean improvedVersionAllowed, List<String> observationsToAddress,
                                    List<ReviewRoundResponse> rounds) {

    public static ReviewHistoryResponse from(QuestionReviewHistory result) {
        ReviewHistory history = result.history();
        return new ReviewHistoryResponse(history.questionId(), result.question().text(), result.question().authorId(),
                history.totalRounds(), history.allowsImprovedVersion(), history.observationsToAddress(),
                history.rounds().stream().map(ReviewRoundResponse::from).toList());
    }
}
