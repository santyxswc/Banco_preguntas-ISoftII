package co.unicauca.iso2.taller09.questionservice.controller;

import co.unicauca.iso2.taller09.common.constants.IdentityHeaders;
import co.unicauca.iso2.taller09.common.dto.CreateQuestionRequest;
import co.unicauca.iso2.taller09.common.dto.CreateQuestionResponse;
import co.unicauca.iso2.taller09.common.dto.OperationResultDTO;
import co.unicauca.iso2.taller09.common.dto.QuestionDTO;
import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import co.unicauca.iso2.taller09.questionservice.application.QuestionAuthoringFacade;
import co.unicauca.iso2.taller09.questionservice.client.LifecycleClient;
import co.unicauca.iso2.taller09.questionservice.domain.PageResult;
import co.unicauca.iso2.taller09.questionservice.domain.Question;
import co.unicauca.iso2.taller09.questionservice.domain.QuestionFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * API REST del contexto de Autoría (HU-01, HU-02 y HU-03). Expuesta por el API Gateway
 * en {@code /api/v1/preguntas}. Solo traduce HTTP; la lógica está en {@link QuestionAuthoringFacade}.
 */
@RestController
@RequestMapping("/questions")
@CrossOrigin(origins = "*")
@Tag(name = "Preguntas", description = "Creación, edición, listado paginado y envío a revisión de preguntas")
public class QuestionController {

    private final QuestionAuthoringFacade facade;
    private final LifecycleClient lifecycleClient;

    public QuestionController(QuestionAuthoringFacade facade, LifecycleClient lifecycleClient) {
        this.facade = facade;
        this.lifecycleClient = lifecycleClient;
    }

    @PostMapping
    @Operation(summary = "Crear pregunta (HU-01)",
            description = "Valida síncronamente con validation-service. Si es válida la guarda en DRAFT (201); si no, responde 400 con la lista de errores.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pregunta validada y creada en DRAFT",
                    content = @Content(schema = @Schema(implementation = CreateQuestionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Error de validación estructural", content = @Content),
            @ApiResponse(responseCode = "503", description = "validation-service no responde", content = @Content)
    })
    public ResponseEntity<CreateQuestionResponse> createQuestion(@RequestBody CreateQuestionRequest request) {
        Question saved = facade.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateQuestionResponse(
                saved.getId(), saved.getStatus().name(), "Pregunta validada y persistida exitosamente en estado DRAFT"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar pregunta (HU-03)",
            description = "Solo el autor puede editar, y solo si la pregunta está en DRAFT o REJECTED según lifecycle-service.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pregunta actualizada",
                    content = @Content(schema = @Schema(implementation = QuestionDTO.class))),
            @ApiResponse(responseCode = "400", description = "Error de validación estructural", content = @Content),
            @ApiResponse(responseCode = "403", description = "Quien edita no es el autor", content = @Content),
            @ApiResponse(responseCode = "404", description = "La pregunta no existe", content = @Content),
            @ApiResponse(responseCode = "409", description = "La pregunta no está en un estado editable", content = @Content)
    })
    public ResponseEntity<QuestionDTO> updateQuestion(@PathVariable Long id,
                                                      @RequestParam(required = false) Long requestedBy,
                                                      @RequestHeader(value = IdentityHeaders.USER_ID, required = false) Long userId,
                                                      @RequestBody CreateQuestionRequest request) {
        facade.update(id, request, userId != null ? userId : requestedBy);
        return ResponseEntity.ok(facade.findById(id));
    }

    @PostMapping("/{id}/submit-review")
    @Operation(summary = "Enviar pregunta a revisión de forma asíncrona (HU-02)",
            description = "Publica SubmitQuestionForReview en RabbitMQ hacia lifecycle-service y responde 202 con un operationId.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Solicitud aceptada para procesamiento asíncrono"),
            @ApiResponse(responseCode = "403", description = "Quien envía no es el autor", content = @Content),
            @ApiResponse(responseCode = "404", description = "La pregunta no existe", content = @Content),
            @ApiResponse(responseCode = "503", description = "RabbitMQ no disponible", content = @Content)
    })
    public ResponseEntity<Map<String, Object>> submitForReview(@PathVariable Long id,
                                                               @RequestParam(required = false) Long requestedBy,
                                                               @RequestHeader(value = IdentityHeaders.USER_ID, required = false) Long userId) {
        String operationId = facade.submitForReview(id, userId != null ? userId : requestedBy);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                "operationId", operationId,
                "questionId", id,
                "status", "PENDING",
                "message", "Solicitud de envío a revisión aceptada para procesamiento asíncrono"));
    }

    @GetMapping("/operations/{operationId}")
    @Operation(summary = "Consultar el resultado de una operación asíncrona",
            description = "Permite saber si lifecycle-service completó o rechazó el envío a revisión.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado de la operación",
                    content = @Content(schema = @Schema(implementation = OperationResultDTO.class))),
            @ApiResponse(responseCode = "404", description = "Operación aún no registrada o inexistente", content = @Content)
    })
    public ResponseEntity<?> checkOperationStatus(@PathVariable String operationId) {
        return lifecycleClient.operation(operationId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Operación no encontrada", "operationId", operationId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener el detalle de una pregunta con su estado actual y color")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pregunta encontrada",
                    content = @Content(schema = @Schema(implementation = QuestionDTO.class))),
            @ApiResponse(responseCode = "404", description = "La pregunta no existe", content = @Content)
    })
    public ResponseEntity<QuestionDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(facade.findById(id));
    }

    @GetMapping
    @Operation(summary = "Listar preguntas con filtros y paginación (HU-03)",
            description = "Todos los filtros son opcionales. El estado y su color se consultan en lifecycle-service.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de preguntas"),
            @ApiResponse(responseCode = "400", description = "Parámetros de paginación o de estado inválidos", content = @Content)
    })
    public ResponseEntity<PageResult<QuestionDTO>> search(
            @Parameter(description = "Autor de las preguntas (el autor ve las suyas)") @RequestParam(required = false) Long authorId,
            @Parameter(description = "DRAFT, PENDING_REVIEW, IN_REVIEW, APPROVED, REJECTED o PUBLISHED") @RequestParam(required = false) QuestionStatus status,
            @RequestParam(required = false) String competency,
            @RequestParam(required = false) String topic,
            @Parameter(description = "BAJO, MEDIO o ALTO") @RequestParam(required = false) String difficulty,
            @Parameter(description = "Texto a buscar en contexto, enunciado, tema y subtema") @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        QuestionFilter filter = new QuestionFilter(authorId, status, competency, topic, difficulty, keyword);
        return ResponseEntity.ok(facade.search(filter, page, size));
    }

    @GetMapping("/catalog")
    @Operation(summary = "Catálogos para formularios: competencias, niveles y estados con su color (HU-02)")
    public ResponseEntity<Map<String, Object>> catalog() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("competencies", List.of("Diseño de software", "Ingeniería de requisitos", "Bases de datos",
                "Programación", "Redes y comunicaciones", "Gestión de proyectos"));
        body.put("difficulties", List.of("BAJO", "MEDIO", "ALTO"));
        body.put("states", Arrays.stream(QuestionStatus.values())
                .map(s -> Map.of("code", s.name(), "label", s.label(), "color", s.color(), "editable", s.isEditable()))
                .toList());
        return ResponseEntity.ok(body);
    }
}
