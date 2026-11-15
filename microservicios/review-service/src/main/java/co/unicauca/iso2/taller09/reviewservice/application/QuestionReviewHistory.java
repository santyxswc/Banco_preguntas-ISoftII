package co.unicauca.iso2.taller09.reviewservice.application;

import co.unicauca.iso2.taller09.reviewservice.domain.model.QuestionSummary;
import co.unicauca.iso2.taller09.reviewservice.domain.model.ReviewHistory;

/**
 * Resultado del caso de uso "ver el historial de revisiones" (HU-06): los datos básicos de la
 * pregunta junto con sus rondas de revisión.
 */
public record QuestionReviewHistory(QuestionSummary question, ReviewHistory history) {
}
