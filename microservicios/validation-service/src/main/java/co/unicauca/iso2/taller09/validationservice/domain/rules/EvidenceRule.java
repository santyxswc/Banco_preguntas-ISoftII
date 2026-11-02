package co.unicauca.iso2.taller09.validationservice.domain.rules;

import co.unicauca.iso2.taller09.common.dto.CreateQuestionRequest;
import co.unicauca.iso2.taller09.validationservice.domain.ValidationRule;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Elementos del Diseño Centrado en Evidencia: contexto y justificación con al menos 20
 * caracteres, y bibliografía obligatoria.
 */
@Component
@Order(4)
public class EvidenceRule implements ValidationRule {
    static final int MIN_LENGTH = 20;

    @Override
    public String name() {
        return "Diseño Centrado en Evidencia";
    }

    @Override
    public List<String> check(CreateQuestionRequest q) {
        List<String> errors = new ArrayList<>();
        if (ValidationRule.isBlank(q.getContext()) || q.getContext().trim().length() < MIN_LENGTH) {
            errors.add("El contexto es obligatorio y debe tener al menos " + MIN_LENGTH + " caracteres.");
        }
        if (ValidationRule.isBlank(q.getJustification()) || q.getJustification().trim().length() < MIN_LENGTH) {
            errors.add("La justificación de la respuesta es obligatoria y debe tener al menos " + MIN_LENGTH + " caracteres.");
        }
        if (ValidationRule.isBlank(q.getBibliography())) {
            errors.add("La bibliografía es obligatoria.");
        }
        return errors;
    }
}
