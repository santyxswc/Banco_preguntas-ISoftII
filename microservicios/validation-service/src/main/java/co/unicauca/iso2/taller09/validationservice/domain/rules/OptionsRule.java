package co.unicauca.iso2.taller09.validationservice.domain.rules;

import co.unicauca.iso2.taller09.common.dto.CreateQuestionRequest;
import co.unicauca.iso2.taller09.common.dto.OptionDTO;
import co.unicauca.iso2.taller09.validationservice.domain.ValidationRule;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Exactamente cuatro opciones (A-D), sin opciones vacías, repetidas ni comodines como
 * "Todas las anteriores" o "Ninguna de las anteriores".
 */
@Component
@Order(2)
public class OptionsRule implements ValidationRule {
    static final int REQUIRED_OPTIONS = 4;

    @Override
    public String name() {
        return "Opciones";
    }

    @Override
    public List<String> check(CreateQuestionRequest q) {
        List<OptionDTO> options = q.getOptions();
        if (options == null || options.size() != REQUIRED_OPTIONS) {
            return List.of("La pregunta debe contener exactamente 4 opciones de respuesta (A, B, C, D).");
        }
        List<String> errors = new ArrayList<>();
        Set<String> texts = new HashSet<>();
        for (OptionDTO option : options) {
            if (option == null || ValidationRule.isBlank(option.getText())) {
                errors.add("Ninguna opción de respuesta puede estar vacía.");
                continue;
            }
            String clean = option.getText().trim().toLowerCase();
            if (!texts.add(clean)) {
                errors.add("Las opciones de respuesta no pueden ser repetidas: '" + option.getText() + "'.");
            }
            if (clean.contains("todas las anteriores") || clean.contains("ninguna de las anteriores")) {
                errors.add("Está prohibido utilizar opciones como 'Todas las anteriores' o 'Ninguna de las anteriores'.");
            }
        }
        return errors;
    }
}
