package co.unicauca.iso2.taller09.reviewservice.presentation;

import co.unicauca.iso2.taller09.common.constants.IdentityHeaders;
import co.unicauca.iso2.taller09.common.dto.ReviewEvaluationRequest;
import co.unicauca.iso2.taller09.reviewservice.application.AssignedQuestionView;
import co.unicauca.iso2.taller09.reviewservice.application.ReviewService;
import co.unicauca.iso2.taller09.reviewservice.domain.exception.ReviewException;
import co.unicauca.iso2.taller09.reviewservice.presentation.dto.EvaluationResponse;
import co.unicauca.iso2.taller09.reviewservice.presentation.dto.PageResponse;
import co.unicauca.iso2.taller09.reviewservice.presentation.dto.ReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/revisiones")
@Tag(name = "Revisiones", description = "HU-05: preguntas asignadas, evaluación y observaciones")
public class RevisionController {

    private final ReviewService reviewService;

    public RevisionController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/asignadas")
    @Operation(summary = "Listar, paginadas, las preguntas asignadas al revisor y pendientes de su decisión")
    @ApiResponse(responseCode = "200", description = "Página de preguntas asignadas")
    @ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos")
    public ResponseEntity<PageResponse<AssignedQuestionView>> assigned(@RequestParam(required = false) Long revisorId,
                                                                       @RequestHeader(value = IdentityHeaders.USER_ID, required = false) Long userId,
                                                                       @RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size) {
        Long reviewer = userId != null ? userId : revisorId;
        if (reviewer == null) {
            throw ReviewException.validation("Debe indicar el revisor (revisorId o token de sesión).");
        }
        return ResponseEntity.ok(PageResponse.from(reviewService.listAssigned(reviewer, page, size), v -> v));
    }

    @PostMapping("/evaluar")
    @Operation(summary = "Registrar la decisión del revisor (APPROVED o REJECTED) con observaciones")
    @ApiResponse(responseCode = "200", description = "Evaluación registrada; si era la última, la pregunta pasa a APPROVED o REJECTED")
    @ApiResponse(responseCode = "400", description = "Rechazo sin observaciones o datos incompletos")
    @ApiResponse(responseCode = "403", description = "No tiene asignada esta pregunta")
    @ApiResponse(responseCode = "409", description = "El revisor ya evaluó la pregunta o la transición fue rechazada")
    public ResponseEntity<EvaluationResponse> evaluate(@RequestBody ReviewEvaluationRequest request,
                                                       @RequestHeader(value = IdentityHeaders.USER_ID, required = false) Long userId) {
        Long reviewer = userId != null ? userId : request.getReviewerId();
        return ResponseEntity.ok(EvaluationResponse.from(reviewService.evaluate(request.getQuestionId(),
                reviewer, request.getResult(), request.getComments())));
    }

    @GetMapping("/pregunta/{questionId}")
    @Operation(summary = "Consultar en solo lectura las observaciones de cada revisor sobre una pregunta")
    public ResponseEntity<List<ReviewResponse>> observations(@PathVariable Long questionId) {
        return ResponseEntity.ok(reviewService.observations(questionId).stream().map(ReviewResponse::from).toList());
    }
}
