package co.unicauca.iso2.taller09.lifecycleservice;

import co.unicauca.iso2.taller09.common.dto.QuestionStateDTO;
import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import co.unicauca.iso2.taller09.lifecycleservice.domain.QuestionState;
import co.unicauca.iso2.taller09.lifecycleservice.repository.LifecycleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LifecycleControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private LifecycleRepository lifecycleRepository;

    @Test
    void testAprobarPreguntaEnDraftDebeFallar() {
        // Caso de prueba explícito del taller: "Aprobar una pregunta en DRAFT -> Transición rechazada"
        Long questionId = 999L;
        lifecycleRepository.saveState(new QuestionState(null, questionId, QuestionStatus.DRAFT, "SYSTEM", "Borrador"));

        Map<String, String> request = Map.of(
                "targetState", "APPROVED",
                "changedBy", "Admin",
                "message", "Intento ilegal de aprobar en DRAFT"
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/lifecycle/" + questionId + "/transition", request, Map.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("TRANSICION_NO_PERMITIDA", response.getBody().get("error"));
    }

    @Test
    void testTransicionValidaPendingReviewAInReview() {
        Long questionId = 998L;
        lifecycleRepository.saveState(new QuestionState(null, questionId, QuestionStatus.PENDING_REVIEW, "SYSTEM", "Pendiente"));

        Map<String, String> request = Map.of(
                "targetState", "IN_REVIEW",
                "changedBy", "Admin",
                "message", "Revisor asignado"
        );

        ResponseEntity<QuestionStateDTO> response = restTemplate.postForEntity(
                "/lifecycle/" + questionId + "/transition", request, QuestionStateDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(QuestionStatus.IN_REVIEW, response.getBody().getState());
    }

    @Test
    void testConsultaDeEstadosEnLote() {
        lifecycleRepository.saveState(new QuestionState(null, 997L, QuestionStatus.APPROVED, "SYSTEM", "Aprobada"));

        ResponseEntity<QuestionStateDTO[]> response = restTemplate.getForEntity(
                "/lifecycle?ids=997,996,997", QuestionStateDTO[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().length);
        assertEquals(QuestionStatus.APPROVED, response.getBody()[0].getState());
        assertEquals(QuestionStatus.DRAFT, response.getBody()[1].getState());
    }
}
