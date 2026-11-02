package co.unicauca.iso2.taller09.lifecycleservice.messaging;

import co.unicauca.iso2.taller09.common.messages.QuestionSubmittedForReview;

public interface QuestionEventPublisher {

    void questionSubmittedForReview(QuestionSubmittedForReview event);
}
