package co.unicauca.iso2.taller09.validationservice.domain.rules;

import co.unicauca.iso2.taller09.common.dto.CreateQuestionRequest;
import co.unicauca.iso2.taller09.validationservice.domain.ValidationRule;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Competencia, tema, subtema y nivel de dificultad (BAJO, MEDIO o ALTO) son obligatorios. */
@Component
@Order(5)
public class TaxonomyRule implements ValidationRule {
    static final Set<String> LEVELS = Set.of("BAJO", "MEDIO", "ALTO");

    @Override
    public String name() {
        return "Taxonomía";
    }

    @Override
    public List<String> check(CreateQuestionRequest q) {
        List<String> errors = new ArrayList<>();
        if (ValidationRule.isBlank(q.getCompetency())) {
            errors.add("La competencia es obligatoria.");
        }
        if (ValidationRule.isBlank(q.getTopic())) {
            errors.add("El tema es obligatorio.");
        }
        if (ValidationRule.isBlank(q.getSubtopic())) {
            errors.add("El subtema es obligatorio.");
        }
        if (ValidationRule.isBlank(q.getDifficulty()) || !LEVELS.contains(q.getDifficulty().trim().toUpperCase())) {
            errors.add("El nivel de dificultad debe ser BAJO, MEDIO o ALTO.");
        }
        return errors;
    }
}
