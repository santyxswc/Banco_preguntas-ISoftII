package co.unicauca.iso2.taller09.reviewservice.domain.policy;

import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import co.unicauca.iso2.taller09.reviewservice.domain.model.Review;

import java.util.List;

public interface VerdictPolicy {

    QuestionStatus decide(List<Review> reviews);
}
