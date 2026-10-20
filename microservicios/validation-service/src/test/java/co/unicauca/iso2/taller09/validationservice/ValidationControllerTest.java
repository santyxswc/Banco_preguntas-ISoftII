package co.unicauca.iso2.taller09.validationservice;

import co.unicauca.iso2.taller09.common.dto.CreateQuestionRequest;
import co.unicauca.iso2.taller09.common.dto.OptionDTO;
import co.unicauca.iso2.taller09.common.dto.ValidationResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ValidationControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testPreguntaValida() {
        CreateQuestionRequest req = new CreateQuestionRequest(
                "¿Cuál es el patrón GoF adecuado para desacoplar una jerarquía de clases de su implementación?",
                List.of(
                        new OptionDTO("A", "Patrón Bridge"),
                        new OptionDTO("B", "Patrón Singleton"),
                        new OptionDTO("C", "Patrón Observer"),
                        new OptionDTO("D", "Patrón Decorator")
                ),
                "A",
                2L
        );

        ResponseEntity<ValidationResult> response = restTemplate.postForEntity("/validate", req, ValidationResult.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isValid());
        assertTrue(response.getBody().getErrors().isEmpty());
    }

    @Test
    void testPreguntaInvalidaComodinYFaltanOpciones() {
        CreateQuestionRequest req = new CreateQuestionRequest(
                "Texto corto",
                List.of(
                        new OptionDTO("A", "Opción A"),
                        new OptionDTO("B", "Todas las anteriores")
                ),
                "A",
                2L
        );

        ResponseEntity<ValidationResult> response = restTemplate.postForEntity("/validate", req, ValidationResult.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isValid());
        assertFalse(response.getBody().getErrors().isEmpty());
    }
}
