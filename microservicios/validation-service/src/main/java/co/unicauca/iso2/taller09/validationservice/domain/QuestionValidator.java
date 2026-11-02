package co.unicauca.iso2.taller09.validationservice.domain;

import co.unicauca.iso2.taller09.common.dto.CreateQuestionRequest;
import co.unicauca.iso2.taller09.common.dto.ValidationResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Contexto del patrón Strategy: aplica, en orden, todas las {@link ValidationRule} registradas
 * y acumula sus errores (validación estructural de la HU-01).
 */
@Component
public class QuestionValidator {

    private final List<ValidationRule> rules;

    public QuestionValidator(List<ValidationRule> rules) {
        this.rules = List.copyOf(rules);
    }

    public ValidationResult validate(CreateQuestionRequest request) {
        if (request == null) {
            return ValidationResult.fail(List.of("La solicitud de pregunta no puede ser nula."));
        }
        List<String> errors = new ArrayList<>();
        rules.forEach(rule -> errors.addAll(rule.check(request)));
        return errors.isEmpty() ? ValidationResult.ok() : ValidationResult.fail(errors);
    }

    public List<String> ruleNames() {
        return rules.stream().map(ValidationRule::name).toList();
    }
}
