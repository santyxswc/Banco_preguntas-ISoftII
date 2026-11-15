package co.unicauca.iso2.taller09.questionservice;

import co.unicauca.iso2.taller09.common.dto.CreateQuestionRequest;
import co.unicauca.iso2.taller09.common.dto.QuestionDTO;
import co.unicauca.iso2.taller09.common.dto.ValidationResult;
import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import co.unicauca.iso2.taller09.common.messages.SubmitQuestionForReview;
import co.unicauca.iso2.taller09.questionservice.application.QuestionAuthoringFacade;
import co.unicauca.iso2.taller09.questionservice.domain.PageResult;
import co.unicauca.iso2.taller09.questionservice.domain.Question;
import co.unicauca.iso2.taller09.questionservice.domain.QuestionException;
import co.unicauca.iso2.taller09.questionservice.domain.QuestionFilter;
import co.unicauca.iso2.taller09.questionservice.domain.port.QuestionLifecycle;
import co.unicauca.iso2.taller09.questionservice.repository.InMemoryQuestionRepository;
import co.unicauca.iso2.taller09.questionservice.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias de la fachada de Autoría con dobles en memoria para validation-service,
 * lifecycle-service y RabbitMQ (no requieren levantar ningún servicio).
 */
class QuestionAuthoringFacadeTest {

    private QuestionRepository repository;
    private FakeLifecycle lifecycle;
    private List<SubmitQuestionForReview> published;
    private List<String> validationErrors;
    private QuestionAuthoringFacade facade;

    @BeforeEach
    void setUp() {
        repository = new InMemoryQuestionRepository();
        lifecycle = new FakeLifecycle();
        published = new ArrayList<>();
        validationErrors = new ArrayList<>();
        facade = new QuestionAuthoringFacade(repository,
                req -> validationErrors.isEmpty() ? ValidationResult.ok() : ValidationResult.fail(validationErrors),
                lifecycle, published::add);
    }

    @Test
    @DisplayName("HU-01: una pregunta válida se guarda en DRAFT")
    void crearValida() {
        Question q = facade.create(QuestionBuilderTest.solicitud(2L));
        assertNotNull(q.getId());
        assertEquals(QuestionStatus.DRAFT, q.getStatus());
        assertEquals(1, repository.count());
    }

    @Test
    @DisplayName("HU-01: si la validación falla no se guarda y se informan los errores")
    void crearInvalida() {
        validationErrors.add("La bibliografía es obligatoria.");
        QuestionException e = assertThrows(QuestionException.class, () -> facade.create(QuestionBuilderTest.solicitud(2L)));
        assertEquals(400, e.httpStatus());
        assertEquals(List.of("La bibliografía es obligatoria."), e.errors());
        assertEquals(0, repository.count());
    }

    @Test
    @DisplayName("HU-02: el autor envía su pregunta a revisión y se publica el comando")
    void enviarARevision() {
        Question q = facade.create(QuestionBuilderTest.solicitud(2L));
        String operationId = facade.submitForReview(q.getId(), 2L);
        assertEquals(1, published.size());
        assertEquals(operationId, published.get(0).getOperationId());
        assertEquals(q.getId(), published.get(0).getQuestionId());
    }

    @Test
    @DisplayName("HU-02: otro usuario no puede enviar la pregunta a revisión")
    void enviarAjena() {
        Question q = facade.create(QuestionBuilderTest.solicitud(2L));
        assertEquals(403, assertThrows(QuestionException.class, () -> facade.submitForReview(q.getId(), 3L)).httpStatus());
        assertEquals(404, assertThrows(QuestionException.class, () -> facade.submitForReview(999L, 2L)).httpStatus());
        assertTrue(published.isEmpty());
    }

    @Test
    @DisplayName("HU-03: el autor edita su pregunta en borrador")
    void editarEnBorrador() {
        Question q = facade.create(QuestionBuilderTest.solicitud(2L));
        CreateQuestionRequest cambios = QuestionBuilderTest.solicitud(2L);
        cambios.setTopic("Arquitectura");
        facade.update(q.getId(), cambios, 2L);
        assertEquals("Arquitectura", repository.findById(q.getId()).orElseThrow().getTopic());
    }

    @Test
    @DisplayName("HU-03: no se edita una pregunta en revisión ni una ajena")
    void editarNoPermitido() {
        Question q = facade.create(QuestionBuilderTest.solicitud(2L));
        assertEquals(403, assertThrows(QuestionException.class,
                () -> facade.update(q.getId(), QuestionBuilderTest.solicitud(3L), 3L)).httpStatus());
        lifecycle.states.put(q.getId(), QuestionStatus.IN_REVIEW);
        assertEquals(409, assertThrows(QuestionException.class,
                () -> facade.update(q.getId(), QuestionBuilderTest.solicitud(2L), 2L)).httpStatus());
    }

    @Test
    @DisplayName("HU-03: listado paginado y filtrado por el estado que reporta lifecycle-service")
    void listarPaginadoYPorEstado() {
        for (int i = 0; i < 12; i++) {
            facade.create(QuestionBuilderTest.solicitud(2L));
        }
        facade.create(QuestionBuilderTest.solicitud(3L));
        Long primera = repository.findByAuthorId(2L).get(0).getId();
        lifecycle.states.put(primera, QuestionStatus.APPROVED);

        PageResult<QuestionDTO> pagina2 = facade.search(new QuestionFilter(2L, null, null, null, null, null), 1, 5);
        assertEquals(12, pagina2.totalElements());
        assertEquals(3, pagina2.totalPages());
        assertEquals(5, pagina2.content().size());

        PageResult<QuestionDTO> aprobadas = facade.search(
                new QuestionFilter(2L, QuestionStatus.APPROVED, null, null, null, null), 0, 10);
        assertEquals(1, aprobadas.totalElements());
        assertEquals("#28A745", aprobadas.content().get(0).getStatusColor());
        assertEquals(2, lifecycle.batchCalls, "el estado se consulta en una sola llamada por listado");
    }

    @Test
    @DisplayName("HU-03: si lifecycle-service no responde, el listado usa el estado local")
    void listarSinLifecycle() {
        facade.create(QuestionBuilderTest.solicitud(2L));
        lifecycle.down = true;
        PageResult<QuestionDTO> page = facade.search(QuestionFilter.none(), 0, 10);
        assertEquals(QuestionStatus.DRAFT, page.content().get(0).getStatus());
    }

    @Test
    @DisplayName("La paginación rechaza tamaños inválidos")
    void paginacionInvalida() {
        assertEquals(400, assertThrows(QuestionException.class,
                () -> facade.search(QuestionFilter.none(), 0, 0)).httpStatus());
        assertEquals(400, assertThrows(QuestionException.class,
                () -> facade.search(QuestionFilter.none(), -1, 10)).httpStatus());
    }

    @Test
    @DisplayName("HU-06: editar una pregunta rechazada abre la versión 2 y la devuelve a borrador")
    void versionMejoradaTrasRechazo() {
        Question q = facade.create(QuestionBuilderTest.solicitud(2L));
        lifecycle.states.put(q.getId(), QuestionStatus.REJECTED);
        CreateQuestionRequest cambios = QuestionBuilderTest.solicitud(2L);
        cambios.setTopic("Arquitectura hexagonal");

        Question mejorada = facade.update(q.getId(), cambios, 2L);

        assertEquals(2, mejorada.getVersion());
        assertTrue(mejorada.isImprovedVersion());
        assertEquals("Arquitectura hexagonal", mejorada.getTopic());
        assertEquals(QuestionStatus.DRAFT, lifecycle.states.get(q.getId()));
        assertEquals(List.of(q.getId() + ":v2"), lifecycle.reopened);
        assertEquals(2, facade.findById(q.getId()).getVersion());
    }

    @Test
    @DisplayName("HU-06: seguir editando el borrador de la versión mejorada no aumenta la versión")
    void edicionesDelBorradorNoAumentanLaVersion() {
        Question q = facade.create(QuestionBuilderTest.solicitud(2L));
        lifecycle.states.put(q.getId(), QuestionStatus.REJECTED);
        facade.update(q.getId(), QuestionBuilderTest.solicitud(2L), 2L);

        Question otraEdicion = facade.update(q.getId(), QuestionBuilderTest.solicitud(2L), 2L);

        assertEquals(2, otraEdicion.getVersion());
        assertEquals(1, lifecycle.reopened.size());
    }

    @Test
    @DisplayName("HU-06: una pregunta rechazada no se reenvía sin crear antes la versión mejorada")
    void rechazadaNoSeReenviaSinCorregir() {
        Question q = facade.create(QuestionBuilderTest.solicitud(2L));
        lifecycle.states.put(q.getId(), QuestionStatus.REJECTED);

        assertEquals(409, assertThrows(QuestionException.class, () -> facade.submitForReview(q.getId(), 2L)).httpStatus());
        assertTrue(published.isEmpty());

        facade.update(q.getId(), QuestionBuilderTest.solicitud(2L), 2L);
        facade.submitForReview(q.getId(), 2L);

        assertEquals(1, published.size());
        assertEquals(2, published.get(0).getVersion());
    }

    @Test
    @DisplayName("HU-06: si la versión mejorada es inválida no se abre ni cambia el estado")
    void versionMejoradaInvalidaNoCambiaNada() {
        Question q = facade.create(QuestionBuilderTest.solicitud(2L));
        lifecycle.states.put(q.getId(), QuestionStatus.REJECTED);
        validationErrors.add("La justificación es obligatoria.");

        assertEquals(400, assertThrows(QuestionException.class,
                () -> facade.update(q.getId(), QuestionBuilderTest.solicitud(2L), 2L)).httpStatus());

        assertEquals(1, repository.findById(q.getId()).orElseThrow().getVersion());
        assertEquals(QuestionStatus.REJECTED, lifecycle.states.get(q.getId()));
        assertTrue(lifecycle.reopened.isEmpty());
    }

    static class FakeLifecycle implements QuestionLifecycle {
        final Map<Long, QuestionStatus> states = new HashMap<>();
        final List<String> reopened = new ArrayList<>();
        boolean down;
        int batchCalls;

        @Override
        public QuestionStatus stateOf(Long questionId) {
            if (down) throw QuestionException.unavailable("caído");
            return states.getOrDefault(questionId, QuestionStatus.DRAFT);
        }

        @Override
        public void reopenForImprovement(Long questionId, Long authorId, int version) {
            if (down) throw QuestionException.unavailable("caído");
            reopened.add(questionId + ":v" + version);
            states.put(questionId, QuestionStatus.DRAFT);
        }

        @Override
        public Map<Long, QuestionStatus> statesOf(Collection<Long> ids) {
            if (down) throw QuestionException.unavailable("caído");
            batchCalls++;
            Map<Long, QuestionStatus> result = new HashMap<>();
            ids.forEach(id -> result.put(id, states.getOrDefault(id, QuestionStatus.DRAFT)));
            return result;
        }
    }
}
