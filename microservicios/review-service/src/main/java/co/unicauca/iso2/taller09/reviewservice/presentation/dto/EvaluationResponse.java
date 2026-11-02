package co.unicauca.iso2.taller09.reviewservice.presentation.dto;

import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import co.unicauca.iso2.taller09.reviewservice.application.EvaluationOutcome;

public record EvaluationResponse(String message, ReviewResponse review, QuestionStatus questionFinalState,
                                 String stateColor) {

    public static final String MENSAJE_OK = "Evaluación registrada correctamente";

    public static EvaluationResponse from(EvaluationOutcome outcome) {
        QuestionStatus state = outcome.questionFinalState();
        return new EvaluationResponse(MENSAJE_OK, ReviewResponse.from(outcome.review()), state,
                StateColors.of(state));
    }
}
