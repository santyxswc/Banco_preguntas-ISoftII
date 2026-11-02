package co.unicauca.iso2.taller09.reviewservice;

import co.unicauca.iso2.taller09.common.enums.AssignmentStatus;
import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import co.unicauca.iso2.taller09.common.enums.ReviewResult;
import co.unicauca.iso2.taller09.common.messages.ReviewCompleted;
import co.unicauca.iso2.taller09.reviewservice.application.AssignedQuestionView;
import co.unicauca.iso2.taller09.reviewservice.application.EvaluationOutcome;
import co.unicauca.iso2.taller09.reviewservice.application.ReviewService;
import co.unicauca.iso2.taller09.reviewservice.domain.exception.ReviewException;
import co.unicauca.iso2.taller09.reviewservice.domain.model.PageResult;
import co.unicauca.iso2.taller09.reviewservice.domain.model.Review;
import co.unicauca.iso2.taller09.reviewservice.domain.model.ReviewAssignment;
import co.unicauca.iso2.taller09.reviewservice.domain.policy.UnanimousApprovalPolicy;
import co.unicauca.iso2.taller09.reviewservice.support.FakeCollaborators;
import co.unicauca.iso2.taller09.reviewservice.support.InMemoryReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReviewServiceTest {

    private static final Long PREGUNTA = 100L;
    private static final Long AUTOR = 2L;
    private static final Long ADMIN = 1L;
    private static final Long REVISOR_A = 4L;
    private static final Long REVISOR_B = 5L;
    private static final Long REVISOR_C = 3L;
    private static final Long ESTUDIANTE = 6L;

    private InMemoryReviewRepository repository;
    private FakeCollaborators.Questions questions;
    private FakeCollaborators.Users users;
    private FakeCollaborators.Lifecycle lifecycle;
    private FakeCollaborators.Events events;
    private ReviewService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryReviewRepository();
        questions = new FakeCollaborators.Questions().add(PREGUNTA, AUTOR);
        users = new FakeCollaborators.Users()
                .add(AUTOR, "AUTOR", "DOCENTE")
                .add(REVISOR_A, "REVISOR", "DOCENTE")
                .add(REVISOR_B, "REVISOR", "DOCENTE")
                .add(REVISOR_C, "AUTOR", "DOCENTE")
                .add(9L, "REVISOR")
                .add(ESTUDIANTE, "ESTUDIANTE");
        lifecycle = new FakeCollaborators.Lifecycle().set(PREGUNTA, QuestionStatus.PENDING_REVIEW);
        events = new FakeCollaborators.Events();
        service = new ReviewService(repository, questions, users, lifecycle, events, new UnanimousApprovalPolicy());
    }

    private ReviewException.Type typeOf(Runnable action) {
        return assertThrows(ReviewException.class, action::run).getType();
    }

    @Test
    @DisplayName("Asigna de 1 a 3 revisores, pasa la pregunta a IN_REVIEW y publica RevisorAsignado por cada uno")
    void asignaRevisores() {
        List<ReviewAssignment> saved = service.assignReviewers(PREGUNTA, List.of(REVISOR_A, REVISOR_B), ADMIN);

        assertEquals(2, saved.size());
        assertTrue(saved.stream().allMatch(ReviewAssignment::isPending));
        assertEquals(QuestionStatus.IN_REVIEW, lifecycle.stateOf(PREGUNTA));
        assertEquals(2, events.assigned.size());
        assertEquals(REVISOR_A, events.assigned.get(0).getReviewerId());
        assertEquals(ADMIN, events.assigned.get(0).getAssignedBy());
    }

    @Test
    @DisplayName("Rechaza cero o más de tres revisores")
    void limitesDeRevisores() {
        assertEquals(ReviewException.Type.VALIDATION, typeOf(() -> service.assignReviewers(PREGUNTA, List.of(), ADMIN)));
        assertEquals(ReviewException.Type.VALIDATION, typeOf(() -> service.assignReviewers(PREGUNTA, null, ADMIN)));
        assertEquals(ReviewException.Type.VALIDATION, typeOf(() ->
                service.assignReviewers(PREGUNTA, List.of(REVISOR_A, REVISOR_B, REVISOR_C, 9L), ADMIN)));
        assertTrue(lifecycle.transitions.isEmpty());
    }

    @Test
    @DisplayName("Acepta exactamente tres revisores")
    void tresRevisores() {
        assertEquals(3, service.assignReviewers(PREGUNTA, List.of(REVISOR_A, REVISOR_B, REVISOR_C), ADMIN).size());
    }

    @Test
    @DisplayName("No permite repetir un revisor")
    void revisorRepetido() {
        assertEquals(ReviewException.Type.VALIDATION,
                typeOf(() -> service.assignReviewers(PREGUNTA, Arrays.asList(REVISOR_A, REVISOR_A), ADMIN)));
    }

    @Test
    @DisplayName("El autor no puede ser revisor de su propia pregunta")
    void autorNoEsRevisor() {
        ReviewException e = assertThrows(ReviewException.class,
                () -> service.assignReviewers(PREGUNTA, List.of(AUTOR), ADMIN));
        assertEquals(ReviewException.Type.VALIDATION, e.getType());
        assertTrue(e.getMessage().contains("autor"));
        assertTrue(repository.findAllAssignments().isEmpty());
    }

    @Test
    @DisplayName("Rechaza usuarios sin rol de revisor y usuarios inexistentes")
    void revisorInvalido() {
        assertEquals(ReviewException.Type.VALIDATION, typeOf(() -> service.assignReviewers(PREGUNTA, List.of(ESTUDIANTE), ADMIN)));
        assertEquals(ReviewException.Type.NOT_FOUND, typeOf(() -> service.assignReviewers(PREGUNTA, List.of(77L), ADMIN)));
    }

    @Test
    @DisplayName("Solo asigna revisores a preguntas existentes en PENDING_REVIEW")
    void estadoNoElegible() {
        lifecycle.set(PREGUNTA, QuestionStatus.DRAFT);
        assertEquals(ReviewException.Type.CONFLICT, typeOf(() -> service.assignReviewers(PREGUNTA, List.of(REVISOR_A), ADMIN)));
        assertEquals(ReviewException.Type.NOT_FOUND, typeOf(() -> service.assignReviewers(999L, List.of(REVISOR_A), ADMIN)));
        assertTrue(events.assigned.isEmpty());
    }

    @Test
    @DisplayName("Si el ciclo de vida rechaza la transición no se guarda ninguna asignación")
    void transicionRechazadaNoGuarda() {
        lifecycle.setRejectTransitions(true);
        assertEquals(ReviewException.Type.CONFLICT, typeOf(() -> service.assignReviewers(PREGUNTA, List.of(REVISOR_A), ADMIN)));
        assertTrue(repository.findAllAssignments().isEmpty());
        assertTrue(events.assigned.isEmpty());
    }

    @Test
    @DisplayName("Si el servicio de preguntas está caído responde no disponible")
    void servicioPreguntasCaido() {
        questions.setDown(true);
        assertEquals(ReviewException.Type.UNAVAILABLE, typeOf(() -> service.assignReviewers(PREGUNTA, List.of(REVISOR_A), ADMIN)));
    }

    @Test
    @DisplayName("Lista paginada solo las preguntas pendientes del revisor")
    void listaAsignadasPaginada() {
        questions.add(101L, AUTOR).add(102L, AUTOR);
        lifecycle.set(101L, QuestionStatus.PENDING_REVIEW).set(102L, QuestionStatus.PENDING_REVIEW);
        service.assignReviewers(PREGUNTA, List.of(REVISOR_A), ADMIN);
        service.assignReviewers(101L, List.of(REVISOR_A, REVISOR_B), ADMIN);
        service.assignReviewers(102L, List.of(REVISOR_B), ADMIN);

        PageResult<AssignedQuestionView> page0 = service.listAssigned(REVISOR_A, 0, 1);
        assertEquals(2, page0.totalElements());
        assertEquals(2, page0.totalPages());
        assertEquals(1, page0.content().size());
        assertEquals("Pregunta " + PREGUNTA, page0.content().get(0).questionText());

        service.evaluate(PREGUNTA, REVISOR_A, ReviewResult.APPROVED, null);
        assertEquals(1, service.listAssigned(REVISOR_A, 0, 10).totalElements());
    }

    @Test
    @DisplayName("Valida los parámetros de paginación")
    void paginacionInvalida() {
        assertEquals(ReviewException.Type.VALIDATION, typeOf(() -> service.listAssigned(REVISOR_A, -1, 10)));
        assertEquals(ReviewException.Type.VALIDATION, typeOf(() -> service.listAssigned(REVISOR_A, 0, 0)));
        assertEquals(ReviewException.Type.VALIDATION, typeOf(() -> service.listAssigned(REVISOR_A, 0, 51)));
        assertEquals(ReviewException.Type.VALIDATION, typeOf(() -> service.listAssigned(null, 0, 10)));
    }

    @Test
    @DisplayName("Un revisor sin la asignación recibe 403 'No tiene asignada esta pregunta'")
    void evaluarSinAsignacion() {
        service.assignReviewers(PREGUNTA, List.of(REVISOR_A), ADMIN);
        ReviewException e = assertThrows(ReviewException.class,
                () -> service.evaluate(PREGUNTA, REVISOR_B, ReviewResult.APPROVED, null));
        assertEquals(ReviewException.Type.FORBIDDEN, e.getType());
        assertEquals(ReviewService.MENSAJE_NO_ASIGNADA, e.getMessage());
    }

    @Test
    @DisplayName("Rechazar sin observaciones no guarda la evaluación")
    void rechazoSinObservaciones() {
        service.assignReviewers(PREGUNTA, List.of(REVISOR_A), ADMIN);
        ReviewException e = assertThrows(ReviewException.class,
                () -> service.evaluate(PREGUNTA, REVISOR_A, ReviewResult.REJECTED, "   "));
        assertEquals(Review.MENSAJE_RECHAZO_SIN_OBSERVACION, e.getMessage());
        assertTrue(repository.findReviewsByQuestion(PREGUNTA).isEmpty());
        assertTrue(repository.findLatestAssignment(PREGUNTA, REVISOR_A).orElseThrow().isPending());
    }

    @Test
    @DisplayName("Con varios revisores la pregunta sigue en revisión hasta la última decisión")
    void evaluacionParcial() {
        service.assignReviewers(PREGUNTA, List.of(REVISOR_A, REVISOR_B), ADMIN);

        EvaluationOutcome outcome = service.evaluate(PREGUNTA, REVISOR_A, ReviewResult.APPROVED, "Bien planteada");

        assertNull(outcome.questionFinalState());
        assertEquals(QuestionStatus.IN_REVIEW, lifecycle.stateOf(PREGUNTA));
        assertEquals(AssignmentStatus.COMPLETED, repository.findLatestAssignment(PREGUNTA, REVISOR_A).orElseThrow().getStatus());
        assertEquals(1, events.completed.size());
        assertNull(events.completed.get(0).getQuestionFinalState());
    }

    @Test
    @DisplayName("Si todos aprueban la pregunta pasa a APPROVED")
    void todosAprueban() {
        service.assignReviewers(PREGUNTA, List.of(REVISOR_A, REVISOR_B), ADMIN);
        service.evaluate(PREGUNTA, REVISOR_A, ReviewResult.APPROVED, null);

        EvaluationOutcome outcome = service.evaluate(PREGUNTA, REVISOR_B, ReviewResult.APPROVED, "Lista para simulacro");

        assertEquals(QuestionStatus.APPROVED, outcome.questionFinalState());
        assertEquals(QuestionStatus.APPROVED, lifecycle.stateOf(PREGUNTA));
        ReviewCompleted last = events.completed.get(1);
        assertEquals(QuestionStatus.APPROVED, last.getQuestionFinalState());
        assertEquals(AUTOR, last.getAuthorId());
        assertEquals(REVISOR_B, last.getReviewerId());
    }

    @Test
    @DisplayName("Un solo rechazo deja la pregunta en REJECTED y las observaciones quedan visibles")
    void unRechazo() {
        service.assignReviewers(PREGUNTA, List.of(REVISOR_A, REVISOR_B), ADMIN);
        service.evaluate(PREGUNTA, REVISOR_A, ReviewResult.REJECTED, "La opción C delata la clave");

        EvaluationOutcome outcome = service.evaluate(PREGUNTA, REVISOR_B, ReviewResult.APPROVED, null);

        assertEquals(QuestionStatus.REJECTED, outcome.questionFinalState());
        List<Review> observations = service.observations(PREGUNTA);
        assertEquals(2, observations.size());
        assertEquals("La opción C delata la clave", observations.get(0).getComments());
    }

    @Test
    @DisplayName("Un revisor no puede evaluar dos veces la misma pregunta")
    void evaluacionDuplicada() {
        service.assignReviewers(PREGUNTA, List.of(REVISOR_A, REVISOR_B), ADMIN);
        service.evaluate(PREGUNTA, REVISOR_A, ReviewResult.APPROVED, null);
        assertEquals(ReviewException.Type.CONFLICT,
                typeOf(() -> service.evaluate(PREGUNTA, REVISOR_A, ReviewResult.APPROVED, null)));
    }

    @Test
    @DisplayName("Si falla la transición final no se registra la última evaluación")
    void transicionFinalFallida() {
        service.assignReviewers(PREGUNTA, List.of(REVISOR_A), ADMIN);
        lifecycle.setRejectTransitions(true);

        assertEquals(ReviewException.Type.CONFLICT,
                typeOf(() -> service.evaluate(PREGUNTA, REVISOR_A, ReviewResult.APPROVED, null)));
        assertTrue(repository.findReviewsByQuestion(PREGUNTA).isEmpty());
        assertTrue(repository.findLatestAssignment(PREGUNTA, REVISOR_A).orElseThrow().isPending());
        assertTrue(events.completed.isEmpty());
    }

    @Test
    @DisplayName("La decisión es obligatoria")
    void decisionObligatoria() {
        service.assignReviewers(PREGUNTA, List.of(REVISOR_A), ADMIN);
        assertEquals(ReviewException.Type.VALIDATION, typeOf(() -> service.evaluate(PREGUNTA, REVISOR_A, null, "x")));
    }

    @Test
    @DisplayName("Tras un rechazo la pregunta corregida se revisa en una nueva ronda sin arrastrar el rechazo anterior")
    void nuevaRondaTrasRechazo() {
        service.assignReviewers(PREGUNTA, List.of(REVISOR_A), ADMIN);
        service.evaluate(PREGUNTA, REVISOR_A, ReviewResult.REJECTED, "Corregir el contexto");
        lifecycle.set(PREGUNTA, QuestionStatus.PENDING_REVIEW);

        List<ReviewAssignment> ronda2 = service.assignReviewers(PREGUNTA, List.of(REVISOR_A, REVISOR_B), ADMIN);
        assertEquals(2, ronda2.get(0).getRound());
        assertTrue(repository.findLatestAssignment(PREGUNTA, REVISOR_A).orElseThrow().isPending());

        service.evaluate(PREGUNTA, REVISOR_A, ReviewResult.APPROVED, null);
        EvaluationOutcome outcome = service.evaluate(PREGUNTA, REVISOR_B, ReviewResult.APPROVED, null);

        assertEquals(QuestionStatus.APPROVED, outcome.questionFinalState());
        assertEquals(3, service.observations(PREGUNTA).size());
        assertEquals(2, outcome.review().getRound());
    }
}
