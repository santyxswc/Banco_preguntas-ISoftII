package co.unicauca.iso2.taller09.questionservice.controller;

import co.unicauca.iso2.taller09.common.constants.RabbitMQConstants;
import co.unicauca.iso2.taller09.common.dto.CreateQuestionRequest;
import co.unicauca.iso2.taller09.common.dto.CreateQuestionResponse;
import co.unicauca.iso2.taller09.common.dto.OperationResultDTO;
import co.unicauca.iso2.taller09.common.dto.QuestionDTO;
import co.unicauca.iso2.taller09.common.dto.QuestionStateDTO;
import co.unicauca.iso2.taller09.common.dto.ValidationResult;
import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import co.unicauca.iso2.taller09.common.messages.SubmitQuestionForReview;
import co.unicauca.iso2.taller09.questionservice.client.ValidationClient;
import co.unicauca.iso2.taller09.questionservice.domain.Question;
import co.unicauca.iso2.taller09.questionservice.repository.QuestionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/questions")
@CrossOrigin(origins = "*")
@Tag(name = "Preguntas", description = "Endpoints para la gestión, validación y envío a revisión de preguntas")
public class QuestionController {
    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    private final QuestionRepository questionRepository;
    private final ValidationClient validationClient;
    private final RabbitTemplate rabbitTemplate;
    private final RestTemplate restTemplate;
    private final String lifecycleServiceUrl;

    public QuestionController(QuestionRepository questionRepository,
                              ValidationClient validationClient,
                              RabbitTemplate rabbitTemplate,
                              RestTemplate restTemplate,
                              @Value("${services.lifecycle.url}") String lifecycleServiceUrl) {
        this.questionRepository = questionRepository;
        this.validationClient = validationClient;
        this.rabbitTemplate = rabbitTemplate;
        this.restTemplate = restTemplate;
        this.lifecycleServiceUrl = lifecycleServiceUrl;
    }

    /**
     * Paso 2 y 3 del Taller: Creación síncrona + Validación síncrona con Validation Service.
     */
    @PostMapping
    @Operation(summary = "Crear pregunta síncronamente (Puntos 2 y 3)", description = "Solicita validación al Validation Service vía REST. Si es exitosa, persiste en estado DRAFT (201 Created); si falla, retorna 400 Bad Request.")
    public ResponseEntity<?> createQuestion(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody CreateQuestionRequest request) {

        log.info("Recibida solicitud de creación de pregunta. AutorId: {}", request.getAuthorId());

        // Invocación síncrona al Validation Service
        ValidationResult validationResult = validationClient.validateQuestion(request);

        if (!validationResult.isValid()) {
            log.warn("Validación fallida para la pregunta: {}", validationResult.getErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message", "Error de validación estructural",
                            "errors", validationResult.getErrors()
                    ));
        }

        // Persistir en DRAFT
        Question question = new Question(
                null,
                request.getText(),
                request.getOptions(),
                request.getCorrectOptionId(),
                request.getAuthorId() != null ? request.getAuthorId() : 2L,
                QuestionStatus.DRAFT
        );

        Question saved = questionRepository.save(question);
        log.info("Pregunta persistida exitosamente con ID {} en estado DRAFT", saved.getId());

        CreateQuestionResponse response = new CreateQuestionResponse(
                saved.getId(),
                saved.getStatus().name(),
                "Pregunta validada y persistida exitosamente en estado DRAFT"
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Paso 4 y 5 del Taller: Envío asíncrono a revisión mediante comando SubmitQuestionForReview en RabbitMQ.
     */
    @PostMapping("/{id}/submit-review")
    @Operation(summary = "Enviar pregunta a revisión asíncronamente (Puntos 4 y 5)", description = "Publica en RabbitMQ el comando SubmitQuestionForReview hacia Lifecycle Service y responde 202 Accepted con operationId.")
    public ResponseEntity<?> submitForReview(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "2") Long requestedBy) {

        Optional<Question> questionOpt = questionRepository.findById(id);
        if (questionOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "La pregunta con ID " + id + " no existe"));
        }

        Question question = questionOpt.get();

        // Generar identificador único de operación
        String operationId = UUID.randomUUID().toString();

        SubmitQuestionForReview command = new SubmitQuestionForReview(
                operationId,
                id,
                requestedBy,
                LocalDateTime.now()
        );

        log.info("Publicando comando SubmitQuestionForReview en RabbitMQ [routingKey: {}] para questionId: {}, operationId: {}",
                RabbitMQConstants.ROUTING_KEY_QUESTION_SUBMIT, id, operationId);

        // Publicar comando en RabbitMQ
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.EXCHANGE_NAME,
                RabbitMQConstants.ROUTING_KEY_QUESTION_SUBMIT,
                command
        );

        // Retornar 202 Accepted
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                "operationId", operationId,
                "questionId", id,
                "status", "PENDING",
                "message", "Solicitud de envío a revisión aceptada para procesamiento asíncrono"
        ));
    }

    /**
     * Consultar estado de una operación asíncrona (para polling en Postman como en la figura 2).
     */
    @GetMapping("/operations/{operationId}")
    @Operation(summary = "Consultar estado de operación asíncrona", description = "Permite verificar mediante polling si la transición asíncrona en Lifecycle Service se completó o falló.")
    public ResponseEntity<?> checkOperationStatus(@PathVariable String operationId) {
        try {
            String url = lifecycleServiceUrl + "/operations/" + operationId;
            ResponseEntity<OperationResultDTO> response = restTemplate.getForEntity(url, OperationResultDTO.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            log.error("No se pudo obtener el estado de la operación {}: {}", operationId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Operación no encontrada o servicio de ciclo de vida no disponible", "operationId", operationId));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalles de una pregunta por ID")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<Question> questionOpt = questionRepository.findById(id);
        if (questionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Question q = questionOpt.get();

        // Opcionalmente sincronizar estado con Lifecycle Service si está disponible
        try {
            ResponseEntity<QuestionStateDTO> stateResp = restTemplate.getForEntity(
                    lifecycleServiceUrl + "/lifecycle/" + id, QuestionStateDTO.class);
            if (stateResp.getStatusCode().is2xxSuccessful() && stateResp.getBody() != null) {
                q.setStatus(stateResp.getBody().getState());
            }
        } catch (Exception ignored) {}

        return ResponseEntity.ok(q.toDTO());
    }

    @GetMapping
    @Operation(summary = "Listar todas las preguntas")
    public ResponseEntity<List<QuestionDTO>> listAll() {
        List<QuestionDTO> dtos = questionRepository.findAll().stream()
                .map(Question::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
