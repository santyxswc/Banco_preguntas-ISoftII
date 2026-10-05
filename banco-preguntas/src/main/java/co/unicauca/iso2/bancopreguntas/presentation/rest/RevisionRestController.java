/**
 * @file RevisionRestController.java
 * @brief Controlador REST para la revisión por pares de preguntas (HU05).
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.presentation.rest;

import co.unicauca.iso2.bancopreguntas.domain.Question;
import co.unicauca.iso2.bancopreguntas.domain.Revision;
import co.unicauca.iso2.bancopreguntas.domain.facade.BancoPreguntasFacade;
import co.unicauca.iso2.bancopreguntas.presentation.rest.dto.DTOs.ApiResponse;
import co.unicauca.iso2.bancopreguntas.presentation.rest.dto.DTOs.EvaluacionRequest;
import co.unicauca.iso2.bancopreguntas.presentation.rest.dto.DTOs.RevisionResponse;
import co.unicauca.iso2.bancopreguntas.presentation.rest.dto.PreguntaDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/revisiones")
@CrossOrigin(origins = "*")
@Tag(name = "Revisiones", description = "Endpoints para la evaluación por pares de preguntas (HU05)")
public class RevisionRestController {

    private final BancoPreguntasFacade facade;

    public RevisionRestController(BancoPreguntasFacade facade) {
        this.facade = facade;
    }

    @GetMapping("/asignadas/{revisorId}")
    @Operation(summary = "Listar preguntas asignadas a un revisor (HU05)", description = "Retorna las preguntas en estado EN_REVISION que han sido asignadas al revisor especificado.")
    public ResponseEntity<ApiResponse<List<PreguntaDTO>>> listarPreguntasAsignadas(@PathVariable String revisorId) {
        List<Question> preguntas = facade.listarPreguntasAsignadas(revisorId);
        List<PreguntaDTO> dtos = preguntas.stream()
                .map(PreguntaDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(dtos, "Preguntas asignadas al revisor"));
    }

    @PostMapping("/evaluar")
    @Operation(summary = "Registrar evaluación de una pregunta (HU05)", description = "Permite al revisor asignar veredicto APROBADA o RECHAZADA, registrar observaciones y notificar al autor.")
    public ResponseEntity<ApiResponse<Void>> evaluarPregunta(@RequestBody EvaluacionRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error(
                    List.of("Cuerpo de petición requerido"), "Petición inválida"));
        }

        List<String> errores = facade.evaluarPregunta(
                request.getPreguntaId(),
                request.getRevisorId(),
                request.getVeredicto(),
                request.getObservaciones()
        );

        if (!errores.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error(errores, "Error al procesar la evaluación"));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(null, "Evaluación registrada exitosamente. Pregunta actualizada."));
    }

    @GetMapping("/historial/{preguntaId}")
    @Operation(summary = "Obtener historial de revisiones de una pregunta", description = "Retorna todas las revisiones y observaciones registradas para una pregunta.")
    public ResponseEntity<ApiResponse<List<RevisionResponse>>> obtenerHistorial(@PathVariable String preguntaId) {
        List<Revision> revisiones = facade.obtenerHistorialRevisiones(preguntaId);
        List<RevisionResponse> dtos = revisiones.stream()
                .map(RevisionResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(dtos, "Historial de revisiones"));
    }
}
