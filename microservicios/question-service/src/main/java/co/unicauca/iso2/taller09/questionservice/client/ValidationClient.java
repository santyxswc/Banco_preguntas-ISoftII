package co.unicauca.iso2.taller09.questionservice.client;

import co.unicauca.iso2.taller09.common.dto.CreateQuestionRequest;
import co.unicauca.iso2.taller09.common.dto.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class ValidationClient {
    private static final Logger log = LoggerFactory.getLogger(ValidationClient.class);

    private final RestTemplate restTemplate;
    private final String validationServiceUrl;

    public ValidationClient(RestTemplate restTemplate,
                            @Value("${services.validation.url}") String validationServiceUrl) {
        this.restTemplate = restTemplate;
        this.validationServiceUrl = validationServiceUrl;
    }

    public ValidationResult validateQuestion(CreateQuestionRequest request) {
        try {
            log.info("Invocando síncronamente Validation Service en: {}", validationServiceUrl);
            ResponseEntity<ValidationResult> response = restTemplate.postForEntity(
                    validationServiceUrl, request, ValidationResult.class);
            return response.getBody() != null ? response.getBody() : ValidationResult.fail(List.of("Respuesta vacía de validación"));
        } catch (Exception e) {
            log.error("Error al comunicarse con Validation Service: {}", e.getMessage());
            return ValidationResult.fail(List.of("No se pudo comunicar con Validation Service: " + e.getMessage()));
        }
    }
}
