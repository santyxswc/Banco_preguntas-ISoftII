package co.unicauca.iso2.taller09.reviewservice.controller;

import co.unicauca.iso2.taller09.common.constants.RabbitMQConstants;
import co.unicauca.iso2.taller09.common.dto.*;
import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import co.unicauca.iso2.taller09.common.enums.ReviewResult;
import co.unicauca.iso2.taller09.common.messages.ReviewerAssigned;
import co.unicauca.iso2.taller09.reviewservice.client.LifecycleClient;
import co.unicauca.iso2.taller09.reviewservice.domain.Review;
import co.unicauca.iso2.taller09.reviewservice.domain.ReviewAssignment;
import co.unicauca.iso2.taller09.reviewservice.repository.ReviewRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reviews")
@CrossOrigin(origins = "*")
@Tag(name = "Revisiones", description = "Endpoints de asignación de revisores y evaluación por pares")
public class ReviewController {
    private static final Logger log = LoggerFactory.getLogger(ReviewController.class);

    private final ReviewRepository reviewRepository;
    private final LifecycleClient lifecycleClient;
    private final RabbitTemplate rabbitTemplate;

    public ReviewController(ReviewRepository reviewRepository,
                            LifecycleClient lifecycleClient,
                            RabbitTemplate rabbitTemplate) {
        this.reviewRepository = reviewRepository;
        this.lifecycleClient = lifecycleClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Paso 7 y 8 del Taller: Asignación síncrona con Lifecycle Service y Notificación asíncrona con RabbitMQ.
     */
    @PostMapping("/assign")
    @Operation(summary = "Asignar revisor a una pregunta (Puntos 7 y 8)", description = "Verifica elegibilidad, solicita transición síncrona a IN_REVIEW en Lifecycle Service y publica el evento ReviewerAssigned en RabbitMQ.")
    public ResponseEntity<?> assignReviewer(@RequestBody AssignReviewerRequest request) {
        log.info("Recibida solicitud de asignación de revisor: questionId={}, reviewerId={}, assignedBy={}",
                request.getQuestionId(), request.getReviewerId(), request.getAssignedBy());

        if (request.getQuestionId() == null || request.getReviewerId() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "questionId y reviewerId son obligatorios"));
        }

        // 1. Verificación de elegibilidad del estado en Lifecycle Service
        Optional<QuestionStateDTO> stateOpt = lifecycleClient.getQuestionState(request.getQuestionId());
        if (stateOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "message", "No se encontró el estado de la pregunta con ID " + request.getQuestionId()
            ));
        }

        QuestionStateDTO state = stateOpt.get();
        if (state.getState() != QuestionStatus.PENDING_REVIEW) {
            String errorMsg = String.format("Inconsistencia rechazada: La pregunta está en estado %s. Debe estar en PENDING_REVIEW para asignar revisor.", state.getState());
            log.warn(errorMsg);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", "ESTADO_NO_ELEGIBLE",
                    "message", errorMsg,
                    "currentState", state.getState().name()
            ));
        }

        // 2. Solicitar al Lifecycle Service la transición síncrona PENDING_REVIEW -> IN_REVIEW
        boolean transitionSuccess = lifecycleClient.requestTransition(
                request.getQuestionId(),
                "IN_REVIEW",
                "Administrador " + request.getAssignedBy(),
                "Asignación de docente revisor con ID " + request.getReviewerId()
        );

        // Si la transición falla, abortamos para evitar inconsistencias (requisito explícito del PDF)
        if (!transitionSuccess) {
            log.error("Fallo la transición a IN_REVIEW en Lifecycle Service. Abortando registro de asignación para mantener consistencia.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "FALLO_TRANSICION",
                    "message", "No fue posible actualizar el estado en Lifecycle Service. Asignación cancelada."
            ));
        }

        // 3. Registrar la asignación exitosa
        ReviewAssignment assignment = new ReviewAssignment(
                null,
                request.getQuestionId(),
                request.getReviewerId(),
                request.getAssignedBy() != null ? request.getAssignedBy() : 1L
        );
        ReviewAssignment saved = reviewRepository.saveAssignment(assignment);

        // 4. Paso 8: Notificación asíncrona mediante publicación de evento ReviewerAssigned en RabbitMQ
        String operationId = UUID.randomUUID().toString();
        ReviewerAssigned event = new ReviewerAssigned(
                operationId,
                request.getQuestionId(),
                request.getReviewerId(),
                request.getAssignedBy() != null ? request.getAssignedBy() : 1L,
                LocalDateTime.now()
        );

        log.info("Publicando evento asíncrono ReviewerAssigned en RabbitMQ [routingKey: {}] para reviewerId: {}",
                RabbitMQConstants.ROUTING_KEY_REVIEWER_ASSIGNED, request.getReviewerId());

        rabbitTemplate.convertAndSend(
                RabbitMQConstants.EXCHANGE_NAME,
                RabbitMQConstants.ROUTING_KEY_REVIEWER_ASSIGNED,
                event
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "assignment", saved.toDTO(),
                "message", "Revisor asignado con éxito y evento de notificación despachado a RabbitMQ"
        ));
    }

    /**
     * Evaluación de la pregunta (Rechazar o Aprobar pregunta en IN_REVIEW).
     */
    @PostMapping("/evaluate")
    @Operation(summary = "Evaluar pregunta (Aprobar o Rechazar con observaciones)", description = "Permite registrar el veredicto del revisor y transicionar la pregunta a APPROVED o REJECTED.")
    public ResponseEntity<?> evaluateQuestion(@RequestBody ReviewEvaluationRequest request) {
        log.info("Evaluación de pregunta: questionId={}, reviewerId={}, result={}",
                request.getQuestionId(), request.getReviewerId(), request.getResult());

        if (request.getQuestionId() == null || request.getResult() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "questionId y result son obligatorios"));
        }

        // Solicitar transición al Lifecycle Service
        String targetState = request.getResult() == ReviewResult.APPROVED ? "APPROVED" : "REJECTED";
        boolean transitionSuccess = lifecycleClient.requestTransition(
                request.getQuestionId(),
                targetState,
                "Revisor " + request.getReviewerId(),
                request.getComments() != null ? request.getComments() : "Evaluación de revisión"
        );

        if (!transitionSuccess) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "TRANSICION_RECHAZADA",
                    "message", "Lifecycle Service rechazó la transición hacia " + targetState + " (¿la pregunta está en IN_REVIEW?)"
            ));
        }

        // Guardar resultado de revisión
        Review review = new Review(
                null,
                request.getQuestionId(),
                request.getReviewerId(),
                request.getResult(),
                request.getComments()
        );
        Review saved = reviewRepository.saveReview(review);

        return ResponseEntity.ok(Map.of(
                "review", saved.toDTO(),
                "status", targetState,
                "message", "Pregunta evaluada exitosamente con veredicto " + targetState
        ));
    }

    @GetMapping("/assignments")
    @Operation(summary = "Listar todas las asignaciones")
    public ResponseEntity<List<ReviewAssignmentDTO>> listAssignments() {
        List<ReviewAssignmentDTO> dtos = reviewRepository.findAllAssignments().stream()
                .map(ReviewAssignment::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/assignments/question/{questionId}")
    @Operation(summary = "Listar asignaciones de una pregunta específica")
    public ResponseEntity<List<ReviewAssignmentDTO>> listAssignmentsByQuestion(@PathVariable Long questionId) {
        List<ReviewAssignmentDTO> dtos = reviewRepository.findAssignmentsByQuestionId(questionId).stream()
                .map(ReviewAssignment::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/results/question/{questionId}")
    @Operation(summary = "Listar evaluaciones de una pregunta")
    public ResponseEntity<List<ReviewDTO>> listReviewsByQuestion(@PathVariable Long questionId) {
        List<ReviewDTO> dtos = reviewRepository.findReviewsByQuestionId(questionId).stream()
                .map(Review::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
