package co.unicauca.iso2.taller09.validationservice.controller;

import co.unicauca.iso2.taller09.common.dto.CreateQuestionRequest;
import co.unicauca.iso2.taller09.common.dto.ValidationResult;
import co.unicauca.iso2.taller09.validationservice.domain.QuestionValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/validate")
@CrossOrigin(origins = "*")
@Tag(name = "Validación", description = "Endpoints de validación estructural y pedagógica de preguntas (Punto 3 del taller)")
public class ValidationController {

    private final QuestionValidator validator;

    public ValidationController(QuestionValidator validator) {
        this.validator = validator;
    }

    @PostMapping
    @Operation(summary = "Validar pregunta síncronamente", description = "Valida la estructura, cantidad de opciones, distractores y respuesta correcta de una pregunta.")
    public ResponseEntity<ValidationResult> validateQuestion(@RequestBody CreateQuestionRequest request) {
        ValidationResult result = validator.validate(request);
        return ResponseEntity.ok(result);
    }
}
