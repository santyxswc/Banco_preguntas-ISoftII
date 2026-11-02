package co.unicauca.iso2.taller09.questionservice.domain.port;

import co.unicauca.iso2.taller09.common.dto.CreateQuestionRequest;
import co.unicauca.iso2.taller09.common.dto.ValidationResult;

/** Puerto hacia el contexto de Validación estructural (lo implementa un cliente REST). */
public interface StructuralValidator {

    ValidationResult validate(CreateQuestionRequest request);
}
