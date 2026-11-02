package co.unicauca.iso2.taller09.reviewservice.domain.port;

import co.unicauca.iso2.taller09.common.messages.ReviewCompleted;
import co.unicauca.iso2.taller09.common.messages.ReviewerAssigned;

public interface ReviewEventPublisher {

    void reviewerAssigned(ReviewerAssigned event);

    void reviewCompleted(ReviewCompleted event);
}
