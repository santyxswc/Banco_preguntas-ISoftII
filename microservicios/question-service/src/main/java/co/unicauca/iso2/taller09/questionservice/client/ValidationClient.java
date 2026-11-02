package co.unicauca.iso2.taller09.questionservice.client;

import co.unicauca.iso2.taller09.common.dto.CreateQuestionRequest;
import co.unicauca.iso2.taller09.common.dto.ValidationResult;
import co.unicauca.iso2.taller09.questionservice.domain.QuestionException;
import co.unicauca.iso2.taller09.questionservice.domain.port.StructuralValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Patrón GoF <b>Adapter</b> (estructural): adapta la API REST de validation-service a la
 * interfaz {@link StructuralValidator} que espera el dominio de Autoría.
 */
@Component
public class ValidationClient implements StructuralValidator {
    private static final Logger log = LoggerFactory.getLogger(ValidationClient.class);

    private final RestTemplate restTemplate;
    private final String validationServiceUrl;

    public ValidationClient(RestTemplate restTemplate,
                            @Value("${services.validation.url}") String validationServiceUrl) {
        this.restTemplate = restTemplate;
        this.validationServiceUrl = validationServiceUrl;
    }

    @Override
    public ValidationResult validate(CreateQuestionRequest request) {
        try {
            log.info("Invocando síncronamente Validation Service en: {}", validationServiceUrl);
            ResponseEntity<ValidationResult> response = restTemplate.postForEntity(
                    validationServiceUrl, request, ValidationResult.class);
            return response.getBody() != null ? response.getBody() : ValidationResult.fail(List.of("Respuesta vacía de validación"));
        } catch (RestClientException e) {
            log.error("Error al comunicarse con Validation Service: {}", e.getMessage());
            throw QuestionException.unavailable("El servicio de validación no está disponible.");
        }
    }

    /** Nombre histórico usado en el Taller 09. */
    public ValidationResult validateQuestion(CreateQuestionRequest request) {
        return validate(request);
    }
}
