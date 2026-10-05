/**
 * @file RevisionRestControllerTest.java
 * @brief Pruebas de integración web para RevisionRestController (HU05).
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.presentation.rest;

import co.unicauca.iso2.bancopreguntas.domain.EstadoPregunta;
import co.unicauca.iso2.bancopreguntas.domain.PreguntasDePrueba;
import co.unicauca.iso2.bancopreguntas.domain.Question;
import co.unicauca.iso2.bancopreguntas.presentation.rest.dto.DTOs.ApiResponse;
import co.unicauca.iso2.bancopreguntas.presentation.rest.dto.DTOs.AsignacionRequest;
import co.unicauca.iso2.bancopreguntas.presentation.rest.dto.DTOs.CambioEstadoRequest;
import co.unicauca.iso2.bancopreguntas.presentation.rest.dto.DTOs.EvaluacionRequest;
import co.unicauca.iso2.bancopreguntas.presentation.rest.dto.DTOs.RevisionResponse;
import co.unicauca.iso2.bancopreguntas.presentation.rest.dto.PreguntaDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class RevisionRestControllerTest {

    @Autowired
    private PreguntaRestController preguntaController;

    @Autowired
    private AsignacionRestController asignacionController;

    @Autowired
    private RevisionRestController revisionController;

    @Test
    void flujoCompletoEvaluacionPorRevisorHU05() {
        // 1. Crear pregunta
        Question q = PreguntasDePrueba.preguntaValida("autor1");
        PreguntaDTO dto = PreguntaDTO.fromEntity(q);
        ResponseEntity<ApiResponse<PreguntaDTO>> created = preguntaController.crearPregunta(dto);
        String id = created.getBody().getDatos().getId();

        // 2. Pasar a pendiente
        CambioEstadoRequest req = new CambioEstadoRequest();
        req.setNuevoEstado(EstadoPregunta.PENDIENTE_REVISION);
        preguntaController.cambiarEstado(id, req);

        // 3. Asignar revisores
        AsignacionRequest asigReq = new AsignacionRequest();
        asigReq.setPreguntaId(id);
        asigReq.setRevisorIds(List.of("rev1"));
        asignacionController.asignarRevisores(asigReq);

        // 4. Revisor lista sus asignadas
        ResponseEntity<ApiResponse<List<PreguntaDTO>>> asignadas = revisionController.listarPreguntasAsignadas("rev1");
        assertEquals(HttpStatus.OK, asignadas.getStatusCode());
        assertTrue(asignadas.getBody().getDatos().stream().anyMatch(p -> p.getId().equals(id)));

        // 5. Revisor evalúa
        EvaluacionRequest evalReq = new EvaluacionRequest();
        evalReq.setPreguntaId(id);
        evalReq.setRevisorId("rev1");
        evalReq.setVeredicto(EstadoPregunta.APROBADA);
        evalReq.setObservaciones("Excelente redacción y coherencia pedagógica.");

        ResponseEntity<ApiResponse<Void>> evalResp = revisionController.evaluarPregunta(evalReq);
        assertEquals(HttpStatus.CREATED, evalResp.getStatusCode());

        // 6. Consultar historial
        ResponseEntity<ApiResponse<List<RevisionResponse>>> histResp = revisionController.obtenerHistorial(id);
        assertEquals(HttpStatus.OK, histResp.getStatusCode());
        assertEquals(1, histResp.getBody().getDatos().size());
        assertEquals(EstadoPregunta.APROBADA, histResp.getBody().getDatos().get(0).getVeredicto());
    }
}
