package co.unicauca.iso2.taller09.reviewservice.presentation;

import co.unicauca.iso2.taller09.reviewservice.application.ReviewService;
import co.unicauca.iso2.taller09.reviewservice.presentation.dto.AssignReviewersRequest;
import co.unicauca.iso2.taller09.reviewservice.presentation.dto.AssignmentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/asignaciones")
@Tag(name = "Asignaciones", description = "HU-04: asignación de 1 a 3 revisores a preguntas pendientes de revisión")
public class AsignacionController {

    private final ReviewService reviewService;

    public AsignacionController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @Operation(summary = "Asignar entre 1 y 3 revisores a una pregunta en PENDING_REVIEW")
    @ApiResponse(responseCode = "201", description = "Revisores asignados; la pregunta pasa a IN_REVIEW y se publica RevisorAsignado")
    @ApiResponse(responseCode = "400", description = "Cantidad de revisores inválida, revisor sin rol o revisor que es el autor")
    @ApiResponse(responseCode = "404", description = "La pregunta o el revisor no existen")
    @ApiResponse(responseCode = "409", description = "La pregunta no está en PENDING_REVIEW o ya tiene revisores")
    @ApiResponse(responseCode = "503", description = "Un servicio dependiente no responde")
    public ResponseEntity<List<AssignmentResponse>> assign(@RequestBody AssignReviewersRequest request) {
        List<AssignmentResponse> body = reviewService
                .assignReviewers(request.questionId(), request.reviewerIds(), request.assignedBy())
                .stream().map(AssignmentResponse::from).toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping
    @Operation(summary = "Listar asignaciones, opcionalmente filtradas por pregunta")
    public ResponseEntity<List<AssignmentResponse>> list(@RequestParam(required = false) Long questionId) {
        var assignments = questionId == null ? reviewService.allAssignments() : reviewService.assignmentsOf(questionId);
        return ResponseEntity.ok(assignments.stream().map(AssignmentResponse::from).toList());
    }
}
