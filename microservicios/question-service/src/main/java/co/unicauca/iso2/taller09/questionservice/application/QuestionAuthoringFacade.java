package co.unicauca.iso2.taller09.questionservice.application;

import co.unicauca.iso2.taller09.common.dto.CreateQuestionRequest;
import co.unicauca.iso2.taller09.common.dto.QuestionDTO;
import co.unicauca.iso2.taller09.common.dto.ValidationResult;
import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import co.unicauca.iso2.taller09.common.messages.SubmitQuestionForReview;
import co.unicauca.iso2.taller09.questionservice.domain.PageResult;
import co.unicauca.iso2.taller09.questionservice.domain.Question;
import co.unicauca.iso2.taller09.questionservice.domain.QuestionBuilder;
import co.unicauca.iso2.taller09.questionservice.domain.QuestionException;
import co.unicauca.iso2.taller09.questionservice.domain.QuestionFilter;
import co.unicauca.iso2.taller09.questionservice.domain.port.QuestionLifecycle;
import co.unicauca.iso2.taller09.questionservice.domain.port.ReviewRequestPublisher;
import co.unicauca.iso2.taller09.questionservice.domain.port.StructuralValidator;
import co.unicauca.iso2.taller09.questionservice.repository.QuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Patrón GoF <b>Facade</b> (estructural).
 * <p>
 * Crear, editar, listar o enviar a revisión una pregunta exige coordinar cuatro subsistemas:
 * validation-service (REST), lifecycle-service (REST), el repositorio local y RabbitMQ. El
 * controlador REST solo habla con esta fachada, que oculta ese orden de llamadas y las reglas
 * de autoría (solo el autor edita o envía su pregunta, y solo en borrador o rechazada).
 */
@Service
public class QuestionAuthoringFacade {
    private static final Logger log = LoggerFactory.getLogger(QuestionAuthoringFacade.class);
    static final long DEFAULT_AUTHOR_ID = 2L;

    private final QuestionRepository repository;
    private final StructuralValidator validator;
    private final QuestionLifecycle lifecycle;
    private final ReviewRequestPublisher publisher;

    public QuestionAuthoringFacade(QuestionRepository repository, StructuralValidator validator,
                                   QuestionLifecycle lifecycle, ReviewRequestPublisher publisher) {
        this.repository = repository;
        this.validator = validator;
        this.lifecycle = lifecycle;
        this.publisher = publisher;
    }

    /** HU-01: valida contra validation-service y guarda la pregunta en borrador. */
    public Question create(CreateQuestionRequest request) {
        if (request == null) {
            throw QuestionException.badRequest("El cuerpo de la solicitud es obligatorio");
        }
        if (request.getAuthorId() == null) {
            request.setAuthorId(DEFAULT_AUTHOR_ID);
        }
        requireValid(request);
        Question saved = repository.save(QuestionBuilder.from(request).status(QuestionStatus.DRAFT).build());
        log.info("Pregunta {} creada en DRAFT por el autor {}", saved.getId(), saved.getAuthorId());
        return saved;
    }

    /** HU-03: el autor edita su pregunta mientras esté en borrador o haya sido rechazada. */
    public Question update(Long id, CreateQuestionRequest request, Long requestedBy) {
        Question current = repository.findById(id).orElseThrow(() -> QuestionException.notFound(id));
        Long editor = requestedBy != null ? requestedBy : request.getAuthorId();
        if (!current.isAuthoredBy(editor)) {
            throw QuestionException.forbidden("Solo el autor de la pregunta puede editarla");
        }
        QuestionStatus state = lifecycle.stateOf(id);
        if (!state.isEditable()) {
            throw QuestionException.conflict("La pregunta está en estado " + state.label()
                    + " y solo se puede editar en Borrador o Rechazada");
        }
        request.setAuthorId(current.getAuthorId());
        requireValid(request);
        current.updateContentFrom(QuestionBuilder.from(request).build());
        return repository.save(current);
    }

    /**
     * HU-02: publica el comando asíncrono hacia lifecycle-service y devuelve el operationId
     * para consultar después si la transición se completó.
     */
    public String submitForReview(Long id, Long requestedBy) {
        Question question = repository.findById(id).orElseThrow(() -> QuestionException.notFound(id));
        Long requester = requestedBy != null ? requestedBy : question.getAuthorId();
        if (!question.isAuthoredBy(requester)) {
            throw QuestionException.forbidden("Solo el autor puede enviar su pregunta a revisión");
        }
        String operationId = UUID.randomUUID().toString();
        publisher.publish(new SubmitQuestionForReview(operationId, id, requester, LocalDateTime.now()));
        return operationId;
    }

    public QuestionDTO findById(Long id) {
        Question question = repository.findById(id).orElseThrow(() -> QuestionException.notFound(id));
        QuestionDTO dto = question.toDTO();
        dto.setStatus(currentStateOrLocal(question));
        return dto;
    }

    /**
     * HU-03: aplica los filtros de contenido en el repositorio, consulta en lote el estado real
     * en lifecycle-service, filtra por estado y pagina.
     */
    public PageResult<QuestionDTO> search(QuestionFilter filter, int page, int size) {
        List<Question> candidates = repository.findMatching(filter);
        Map<Long, QuestionStatus> states = statesOrLocal(candidates);
        List<QuestionDTO> filtered = candidates.stream()
                .map(q -> {
                    QuestionDTO dto = q.toDTO();
                    dto.setStatus(states.getOrDefault(q.getId(), q.getStatus()));
                    return dto;
                })
                .filter(dto -> filter.matchesStatus(dto.getStatus()))
                .toList();
        return PageResult.of(filtered, page, size);
    }

    private void requireValid(CreateQuestionRequest request) {
        ValidationResult result = validator.validate(request);
        if (!result.isValid()) {
            log.warn("Validación fallida: {}", result.getErrors());
            throw QuestionException.invalid(result.getErrors());
        }
    }

    private QuestionStatus currentStateOrLocal(Question question) {
        try {
            return lifecycle.stateOf(question.getId());
        } catch (QuestionException e) {
            return question.getStatus();
        }
    }

    /** Si lifecycle-service no responde, el listado sigue funcionando con el estado local. */
    private Map<Long, QuestionStatus> statesOrLocal(List<Question> questions) {
        try {
            return lifecycle.statesOf(questions.stream().map(Question::getId).toList());
        } catch (QuestionException e) {
            log.warn("Listando con el estado local: {}", e.getMessage());
            return Map.of();
        }
    }
}
