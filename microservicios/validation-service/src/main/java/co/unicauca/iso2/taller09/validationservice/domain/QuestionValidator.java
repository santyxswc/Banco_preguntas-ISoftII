package co.unicauca.iso2.taller09.validationservice.domain;

import co.unicauca.iso2.taller09.common.dto.CreateQuestionRequest;
import co.unicauca.iso2.taller09.common.dto.OptionDTO;
import co.unicauca.iso2.taller09.common.dto.ValidationResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class QuestionValidator {

    public ValidationResult validate(CreateQuestionRequest request) {
        List<String> errors = new ArrayList<>();

        if (request == null) {
            errors.add("La solicitud de pregunta no puede ser nula.");
            return ValidationResult.fail(errors);
        }

        // Regla 1: Texto de la pregunta no vacío y longitud mínima
        if (request.getText() == null || request.getText().trim().isEmpty()) {
            errors.add("El texto de la pregunta es obligatorio.");
        } else if (request.getText().trim().length() < 15) {
            errors.add("El texto de la pregunta debe contener al menos 15 caracteres.");
        }

        // Regla 2: Exactamente 4 opciones de respuesta
        List<OptionDTO> options = request.getOptions();
        if (options == null || options.size() != 4) {
            errors.add("La pregunta debe contener exactamente 4 opciones de respuesta (A, B, C, D).");
        } else {
            Set<String> optionTexts = new HashSet<>();
            Set<String> optionIds = new HashSet<>();

            for (OptionDTO opt : options) {
                if (opt == null || opt.getText() == null || opt.getText().trim().isEmpty()) {
                    errors.add("Ninguna opción de respuesta puede estar vacía.");
                } else {
                    String cleanText = opt.getText().trim().toLowerCase();
                    // Regla 3: No duplicados
                    if (!optionTexts.add(cleanText)) {
                        errors.add("Las opciones de respuesta no pueden ser repetidas: '" + opt.getText() + "'.");
                    }

                    // Regla 4: Prohibido comodines
                    if (cleanText.contains("todas las anteriores") || cleanText.contains("ninguna de las anteriores")) {
                        errors.add("Está prohibido utilizar opciones como 'Todas las anteriores' o 'Ninguna de las anteriores'.");
                    }
                }

                if (opt != null && opt.getId() != null) {
                    optionIds.add(opt.getId());
                }
            }

            // Regla 5: Identificación de la respuesta correcta
            if (request.getCorrectOptionId() == null || request.getCorrectOptionId().trim().isEmpty()) {
                errors.add("Debe indicarse el identificador de la respuesta correcta (correctOptionId).");
            } else if (!optionIds.contains(request.getCorrectOptionId())) {
                errors.add("La respuesta correcta (" + request.getCorrectOptionId() + ") no coincide con ninguna de las opciones provistas.");
            }
        }

        if (errors.isEmpty()) {
            return ValidationResult.ok();
        } else {
            return ValidationResult.fail(errors);
        }
    }
}
