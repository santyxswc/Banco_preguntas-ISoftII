package co.unicauca.iso2.taller09.lifecycleservice.controller;

import co.unicauca.iso2.taller09.common.dto.OperationResultDTO;
import co.unicauca.iso2.taller09.common.dto.QuestionStateDTO;
import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import co.unicauca.iso2.taller09.lifecycleservice.domain.OperationResult;
import co.unicauca.iso2.taller09.lifecycleservice.domain.QuestionState;
import co.unicauca.iso2.taller09.lifecycleservice.repository.LifecycleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@Tag(name = "Ciclo de Vida", description = "Autoridad única del estado y transiciones de preguntas (HU-05)")
public class LifecycleController {
    private static final Logger log = LoggerFactory.getLogger(LifecycleController.class);

    private final LifecycleRepository lifecycleRepository;

    public LifecycleController(LifecycleRepository lifecycleRepository) {
        this.lifecycleRepository = lifecycleRepository;
    }

    @GetMapping("/lifecycle/{questionId}")
    @Operation(summary = "Obtener estado actual de una pregunta", description = "Retorna la autoridad de estado para la pregunta indicada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado obtenido exitosamente",
                    content = @Content(schema = @Schema(implementation = QuestionStateDTO.class))),
            @ApiResponse(responseCode = "400", description = "Identificador de pregunta no válido", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado para consultar el estado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pregunta no encontrada", content = @Content)
    })
    public ResponseEntity<QuestionStateDTO> getQuestionState(@PathVariable Long questionId) {
        QuestionState state = lifecycleRepository.getOrCreateState(questionId);
        return ResponseEntity.ok(state.toDTO());
    }

    @GetMapping("/lifecycle")
    @Operation(summary = "Obtener el estado de varias preguntas en una sola consulta",
            description = "Lo usa question-service al listar preguntas paginadas (HU-03): evita una llamada REST por cada pregunta.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estados obtenidos; las preguntas sin historial aparecen en DRAFT"),
            @ApiResponse(responseCode = "400", description = "Parámetro ids ausente o mal formado", content = @Content)
    })
    public ResponseEntity<List<QuestionStateDTO>> getQuestionStates(@RequestParam List<Long> ids) {
        List<QuestionStateDTO> states = ids.stream()
                .distinct()
                .map(lifecycleRepository::getOrCreateState)
                .map(QuestionState::toDTO)
                .toList();
        return ResponseEntity.ok(states);
    }

    @PostMapping("/lifecycle/{questionId}/transition")
    @Operation(summary = "Solicitar transición de estado síncrona (HU-05: Evaluar pregunta)",
            description = "Verifica y ejecuta la transición de estado (ej. PENDING_REVIEW -> IN_REVIEW o IN_REVIEW -> APPROVED/REJECTED).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transición realizada exitosamente",
                    content = @Content(schema = @Schema(implementation = QuestionStateDTO.class))),
            @ApiResponse(responseCode = "400", description = "Transición invalida o estado no permitido para la evaluación", content = @Content),
            @ApiResponse(responseCode = "403", description = "No cuenta con rol de evaluador/revisor para realizar la acción", content = @Content),
            @ApiResponse(responseCode = "404", description = "La pregunta a evaluar no fue encontrada", content = @Content)
    })
    public ResponseEntity<?> transitionState(
            @PathVariable Long questionId,
            @RequestBody Map<String, String> request) {

        String targetStateStr = request.get("targetState");
        String changedBy = request.getOrDefault("changedBy", "SYSTEM");
        String message = request.getOrDefault("message", "Transición manual solicitada");

        if (targetStateStr == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "targetState es obligatorio"));
        }

        QuestionStatus targetState;
        try {
            targetState = QuestionStatus.valueOf(targetStateStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Estado destino inválido: " + targetStateStr));
        }

        QuestionState currentState = lifecycleRepository.getOrCreateState(questionId);

        // Caso de prueba del taller: Aprobar una pregunta en DRAFT -> Transición rechazada
        if (!currentState.canTransitionTo(targetState)) {
            String errorMsg = String.format("Transición rechazada: No se permite pasar del estado %s al estado %s",
                    currentState.getState(), targetState);
            log.warn(errorMsg);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", "TRANSICION_NO_PERMITIDA",
                    "message", errorMsg,
                    "currentState", currentState.getState().name(),
                    "requestedState", targetState.name()
            ));
        }

        currentState.setState(targetState);
        currentState.setChangedBy(changedBy);
        currentState.setMessage(message);
        lifecycleRepository.saveState(currentState);

        log.info("Transición exitosa para questionId={}: nuevo estado es {}", questionId, targetState);
        return ResponseEntity.ok(currentState.toDTO());
    }

    @GetMapping("/operations/{operationId}")
    @Operation(summary = "Consultar resultado de operación asíncrona", description = "Permite a los clientes verificar el avance de operaciones asíncronas (PENDING, COMPLETED, FAILED).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultado de operación consultado con éxito",
                    content = @Content(schema = @Schema(implementation = OperationResultDTO.class))),
            @ApiResponse(responseCode = "400", description = "Formato de operationId inválido", content = @Content),
            @ApiResponse(responseCode = "403", description = "No autorizado para consultar la operación", content = @Content),
            @ApiResponse(responseCode = "404", description = "Operación no encontrada", content = @Content)
    })
    public ResponseEntity<?> getOperationResult(@PathVariable String operationId) {
        Optional<OperationResult> op = lifecycleRepository.findOperationById(operationId);
        if (op.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Operación no encontrada", "operationId", operationId));
        }

        return ResponseEntity.ok(op.get().toDTO());
    }
}