package co.unicauca.iso2.taller09.reviewservice.application;

import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import co.unicauca.iso2.taller09.reviewservice.domain.model.Review;

public record EvaluationOutcome(Review review, QuestionStatus questionFinalState) {

    public boolean questionClosed() {
        return questionFinalState != null;
    }
}
