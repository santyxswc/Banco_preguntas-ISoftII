package co.unicauca.iso2.taller09.lifecycleservice.controller;

import co.unicauca.iso2.taller09.common.dto.OperationResultDTO;
import co.unicauca.iso2.taller09.common.dto.QuestionStateDTO;
import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import co.unicauca.iso2.taller09.lifecycleservice.domain.OperationResult;
import co.unicauca.iso2.taller09.lifecycleservice.domain.QuestionState;
import co.unicauca.iso2.taller09.lifecycleservice.repository.LifecycleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@Tag(name = "Ciclo de Vida", description = "Autoridad única del estado y transiciones de preguntas")
public class LifecycleController {
    private static final Logger log = LoggerFactory.getLogger(LifecycleController.class);

    private final LifecycleRepository lifecycleRepository;

    public LifecycleController(LifecycleRepository lifecycleRepository) {
        this.lifecycleRepository = lifecycleRepository;
    }

    @GetMapping("/lifecycle/{questionId}")
    @Operation(summary = "Obtener estado actual de una pregunta", description = "Retorna la autoridad de estado para la pregunta indicada.")
    public ResponseEntity<QuestionStateDTO> getQuestionState(@PathVariable Long questionId) {
        QuestionState state = lifecycleRepository.getOrCreateState(questionId);
        return ResponseEntity.ok(state.toDTO());
    }

    @PostMapping("/lifecycle/{questionId}/transition")
    @Operation(summary = "Solicitar transición de estado síncrona", description = "Verifica y ejecuta la transición de estado (ej. PENDING_REVIEW -> IN_REVIEW o IN_REVIEW -> APPROVED/REJECTED).")
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
    public ResponseEntity<?> getOperationResult(@PathVariable String operationId) {
        Optional<OperationResult> op = lifecycleRepository.findOperationById(operationId);
        if (op.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Operación no encontrada", "operationId", operationId));
        }

        return ResponseEntity.ok(op.get().toDTO());
    }
}
