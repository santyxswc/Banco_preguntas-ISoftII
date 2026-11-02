package co.unicauca.iso2.taller09.reviewservice.domain.port;

import co.unicauca.iso2.taller09.reviewservice.domain.model.QuestionSummary;

import java.util.Optional;

public interface QuestionCatalog {

    Optional<QuestionSummary> findById(Long questionId);
}
