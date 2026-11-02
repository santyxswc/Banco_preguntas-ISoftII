package co.unicauca.iso2.taller09.questionservice.domain.port;

import co.unicauca.iso2.taller09.common.messages.SubmitQuestionForReview;

/** Puerto de salida asíncrona: publica el comando de envío a revisión (HU-02). */
public interface ReviewRequestPublisher {

    void publish(SubmitQuestionForReview command);
}
