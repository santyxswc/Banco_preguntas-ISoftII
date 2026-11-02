package co.unicauca.iso2.taller09.validationservice.domain.rules;

import co.unicauca.iso2.taller09.common.dto.CreateQuestionRequest;
import co.unicauca.iso2.taller09.common.dto.OptionDTO;
import co.unicauca.iso2.taller09.validationservice.domain.ValidationRule;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/** Hay una única respuesta correcta y corresponde a una de las opciones enviadas. */
@Component
@Order(3)
public class CorrectAnswerRule implements ValidationRule {

    @Override
    public String name() {
        return "Respuesta correcta";
    }

    @Override
    public List<String> check(CreateQuestionRequest q) {
        if (ValidationRule.isBlank(q.getCorrectOptionId())) {
            return List.of("Debe indicarse el identificador de la respuesta correcta (correctOptionId).");
        }
        if (q.getOptions() == null || q.getOptions().size() != OptionsRule.REQUIRED_OPTIONS) {
            return List.of();
        }
        boolean exists = q.getOptions().stream()
                .filter(Objects::nonNull)
                .map(OptionDTO::getId)
                .anyMatch(q.getCorrectOptionId()::equals);
        return exists ? List.of()
                : List.of("La respuesta correcta (" + q.getCorrectOptionId() + ") no coincide con ninguna de las opciones provistas.");
    }
}
