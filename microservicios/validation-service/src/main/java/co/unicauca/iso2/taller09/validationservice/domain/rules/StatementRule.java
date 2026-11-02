package co.unicauca.iso2.taller09.validationservice.domain.rules;

import co.unicauca.iso2.taller09.common.dto.CreateQuestionRequest;
import co.unicauca.iso2.taller09.validationservice.domain.ValidationRule;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/** El enunciado es una pregunta directa: obligatorio y con al menos 15 caracteres. */
@Component
@Order(1)
public class StatementRule implements ValidationRule {
    static final int MIN_LENGTH = 15;

    @Override
    public String name() {
        return "Enunciado";
    }

    @Override
    public List<String> check(CreateQuestionRequest q) {
        if (ValidationRule.isBlank(q.getText())) {
            return List.of("El texto de la pregunta es obligatorio.");
        }
        if (q.getText().trim().length() < MIN_LENGTH) {
            return List.of("El texto de la pregunta debe contener al menos " + MIN_LENGTH + " caracteres.");
        }
        return List.of();
    }
}
