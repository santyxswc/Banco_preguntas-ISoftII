package co.unicauca.iso2.taller09.reviewservice.domain.port;

import co.unicauca.iso2.taller09.common.enums.QuestionStatus;

import java.util.Optional;

public interface QuestionLifecycle {

    Optional<QuestionStatus> currentState(Long questionId);

    void transition(Long questionId, QuestionStatus target, String changedBy, String message);
}
