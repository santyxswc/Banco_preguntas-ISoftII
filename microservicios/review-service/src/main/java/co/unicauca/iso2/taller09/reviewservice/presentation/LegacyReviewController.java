package co.unicauca.iso2.taller09.reviewservice.presentation;

import co.unicauca.iso2.taller09.common.dto.AssignReviewerRequest;
import co.unicauca.iso2.taller09.common.dto.ReviewEvaluationRequest;
import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import co.unicauca.iso2.taller09.reviewservice.application.EvaluationOutcome;
import co.unicauca.iso2.taller09.reviewservice.application.ReviewService;
import co.unicauca.iso2.taller09.reviewservice.presentation.dto.AssignmentResponse;
import co.unicauca.iso2.taller09.reviewservice.presentation.dto.EvaluationResponse;
import co.unicauca.iso2.taller09.reviewservice.presentation.dto.ReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reviews")
@Tag(name = "Revisiones (Taller 09)", description = "Rutas del Taller 09 que delegan en el mismo servicio de revisión")
public class LegacyReviewController {

    private final ReviewService reviewService;

    public LegacyReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/assign")
    @Operation(summary = "Asignar un revisor (ruta del Taller 09)", deprecated = true)
    public ResponseEntity<Map<String, Object>> assign(@RequestBody AssignReviewerRequest request) {
        List<Long> reviewers = request.getReviewerId() == null ? List.of() : List.of(request.getReviewerId());
        Long assignedBy = request.getAssignedBy() != null ? request.getAssignedBy() : 1L;
        AssignmentResponse assignment = AssignmentResponse.from(
                reviewService.assignReviewers(request.getQuestionId(), reviewers, assignedBy).get(0));
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "assignment", assignment,
                "message", "Revisor asignado con éxito y evento de notificación despachado a RabbitMQ"));
    }

    @PostMapping("/evaluate")
    @Operation(summary = "Evaluar una pregunta (ruta del Taller 09)", deprecated = true)
    public ResponseEntity<Map<String, Object>> evaluate(@RequestBody ReviewEvaluationRequest request) {
        EvaluationOutcome outcome = reviewService.evaluate(request.getQuestionId(), request.getReviewerId(),
                request.getResult(), request.getComments());
        QuestionStatus status = outcome.questionClosed() ? outcome.questionFinalState() : QuestionStatus.IN_REVIEW;
        return ResponseEntity.ok(Map.of(
                "review", ReviewResponse.from(outcome.review()),
                "status", status.name(),
                "message", EvaluationResponse.MENSAJE_OK));
    }

    @GetMapping("/assignments")
    @Operation(summary = "Listar todas las asignaciones (ruta del Taller 09)", deprecated = true)
    public ResponseEntity<List<AssignmentResponse>> assignments() {
        return ResponseEntity.ok(reviewService.allAssignments().stream().map(AssignmentResponse::from).toList());
    }

    @GetMapping("/assignments/question/{questionId}")
    @Operation(summary = "Asignaciones de una pregunta (ruta del Taller 09)", deprecated = true)
    public ResponseEntity<List<AssignmentResponse>> assignmentsByQuestion(@PathVariable Long questionId) {
        return ResponseEntity.ok(reviewService.assignmentsOf(questionId).stream().map(AssignmentResponse::from).toList());
    }

    @GetMapping("/results/question/{questionId}")
    @Operation(summary = "Evaluaciones de una pregunta (ruta del Taller 09)", deprecated = true)
    public ResponseEntity<List<ReviewResponse>> results(@PathVariable Long questionId) {
        return ResponseEntity.ok(reviewService.observations(questionId).stream().map(ReviewResponse::from).toList());
    }
}
