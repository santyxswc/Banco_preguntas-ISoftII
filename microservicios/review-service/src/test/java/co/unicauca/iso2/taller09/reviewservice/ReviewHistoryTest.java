package co.unicauca.iso2.taller09.reviewservice;

import co.unicauca.iso2.taller09.common.enums.AssignmentStatus;
import co.unicauca.iso2.taller09.common.enums.ReviewResult;
import co.unicauca.iso2.taller09.reviewservice.domain.exception.ReviewException;
import co.unicauca.iso2.taller09.reviewservice.domain.model.Review;
import co.unicauca.iso2.taller09.reviewservice.domain.model.ReviewAssignment;
import co.unicauca.iso2.taller09.reviewservice.domain.model.ReviewHistory;
import co.unicauca.iso2.taller09.reviewservice.domain.model.ReviewRound;
import co.unicauca.iso2.taller09.reviewservice.domain.model.RoundOutcome;
import co.unicauca.iso2.taller09.reviewservice.domain.policy.UnanimousApprovalPolicy;
import co.unicauca.iso2.taller09.reviewservice.domain.policy.VerdictPolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias del historial de revisiones (HU-06): rondas, resultado de cada ronda y la
 * regla que habilita la versión mejorada. Solo usa clases del dominio, sin Spring ni base de datos.
 */
class ReviewHistoryTest {

    private static final Long PREGUNTA = 100L;
    private static final Long REVISOR_A = 4L;
    private static final Long REVISOR_B = 5L;
    private static final LocalDateTime INICIO = LocalDateTime.of(2026, 11, 10, 8, 0);

    private final VerdictPolicy politica = new UnanimousApprovalPolicy();

    private static ReviewAssignment asignacion(Long revisor, int ronda, AssignmentStatus estado) {
        return new ReviewAssignment(null, PREGUNTA, revisor, 1L, ronda, INICIO.plusDays(ronda), estado);
    }

    private static Review evaluacion(Long revisor, int ronda, ReviewResult decision, String observacion, int hora) {
        return new Review(null, PREGUNTA, revisor, ronda, decision, observacion, INICIO.plusDays(ronda).plusHours(hora));
    }

    @Test
    @DisplayName("Una pregunta sin asignaciones tiene un historial vacío y no admite versión mejorada")
    void historialVacio() {
        ReviewHistory historial = ReviewHistory.of(PREGUNTA, List.of(), List.of(), politica);

        assertTrue(historial.isEmpty());
        assertEquals(0, historial.totalRounds());
        assertTrue(historial.latestRound().isEmpty());
        assertFalse(historial.allowsImprovedVersion());
        assertTrue(historial.observationsToAddress().isEmpty());
    }

    @Test
    @DisplayName("La ronda sigue en curso mientras falte la decisión de algún revisor")
    void rondaEnCurso() {
        ReviewRound ronda = ReviewRound.of(1,
                List.of(asignacion(REVISOR_A, 1, AssignmentStatus.COMPLETED), asignacion(REVISOR_B, 1, AssignmentStatus.ASSIGNED)),
                List.of(evaluacion(REVISOR_A, 1, ReviewResult.REJECTED, "Falta la bibliografía", 1)), politica);

        assertEquals(RoundOutcome.IN_PROGRESS, ronda.outcome());
        assertFalse(ronda.isClosed());
        assertFalse(ronda.isRejected());
        assertEquals(List.of(REVISOR_B), ronda.pendingReviewerIds());
        assertNull(ronda.closedAt());
        assertEquals(INICIO.plusDays(1), ronda.startedAt());
    }

    @Test
    @DisplayName("Con todas las decisiones, la política de unanimidad cierra la ronda")
    void rondaCerradaPorLaPolitica() {
        List<ReviewAssignment> asignaciones = List.of(asignacion(REVISOR_A, 1, AssignmentStatus.COMPLETED),
                asignacion(REVISOR_B, 1, AssignmentStatus.COMPLETED));

        ReviewRound aprobada = ReviewRound.of(1, asignaciones, List.of(
                evaluacion(REVISOR_A, 1, ReviewResult.APPROVED, null, 1),
                evaluacion(REVISOR_B, 1, ReviewResult.APPROVED, "Buena pregunta", 2)), politica);
        assertEquals(RoundOutcome.APPROVED, aprobada.outcome());
        assertEquals(List.of("Buena pregunta"), aprobada.observations());
        assertEquals(INICIO.plusDays(1).plusHours(2), aprobada.closedAt());

        ReviewRound rechazada = ReviewRound.of(1, asignaciones, List.of(
                evaluacion(REVISOR_A, 1, ReviewResult.APPROVED, null, 1),
                evaluacion(REVISOR_B, 1, ReviewResult.REJECTED, "El distractor C es ambiguo", 2)), politica);
        assertEquals(RoundOutcome.REJECTED, rechazada.outcome());
        assertTrue(rechazada.isRejected());
        assertTrue(rechazada.pendingReviewerIds().isEmpty());
    }

    @Test
    @DisplayName("Las evaluaciones de una ronda quedan ordenadas por fecha")
    void evaluacionesOrdenadas() {
        ReviewRound ronda = ReviewRound.of(1,
                List.of(asignacion(REVISOR_A, 1, AssignmentStatus.COMPLETED), asignacion(REVISOR_B, 1, AssignmentStatus.COMPLETED)),
                List.of(evaluacion(REVISOR_B, 1, ReviewResult.APPROVED, null, 5),
                        evaluacion(REVISOR_A, 1, ReviewResult.APPROVED, null, 1)), politica);

        assertEquals(List.of(REVISOR_A, REVISOR_B), ronda.reviews().stream().map(Review::getReviewerId).toList());
    }

    @Test
    @DisplayName("Una ronda exige número válido y al menos un revisor")
    void invariantesDeLaRonda() {
        assertThrows(ReviewException.class, () -> ReviewRound.of(0,
                List.of(asignacion(REVISOR_A, 1, AssignmentStatus.ASSIGNED)), List.of(), politica));
        assertThrows(ReviewException.class, () -> ReviewRound.of(1, List.of(), List.of(), politica));
        assertThrows(NullPointerException.class, () -> ReviewRound.of(1,
                List.of(asignacion(REVISOR_A, 1, AssignmentStatus.ASSIGNED)), List.of(), null));
    }

    @Test
    @DisplayName("El historial agrupa por ronda, en orden, aunque los datos lleguen desordenados")
    void agrupaPorRonda() {
        ReviewHistory historial = ReviewHistory.of(PREGUNTA,
                List.of(asignacion(REVISOR_B, 2, AssignmentStatus.ASSIGNED),
                        asignacion(REVISOR_A, 1, AssignmentStatus.COMPLETED)),
                List.of(evaluacion(REVISOR_A, 1, ReviewResult.REJECTED, "Corregir el contexto", 1)), politica);

        assertEquals(PREGUNTA, historial.questionId());
        assertEquals(2, historial.totalRounds());
        assertEquals(List.of(1, 2), historial.rounds().stream().map(ReviewRound::number).toList());
        assertEquals(RoundOutcome.REJECTED, historial.rounds().get(0).outcome());
        assertEquals(RoundOutcome.IN_PROGRESS, historial.latestRound().orElseThrow().outcome());
    }

    @Test
    @DisplayName("La versión mejorada solo se permite cuando la última ronda fue rechazada")
    void versionMejorada() {
        ReviewHistory rechazada = ReviewHistory.of(PREGUNTA,
                List.of(asignacion(REVISOR_A, 1, AssignmentStatus.COMPLETED)),
                List.of(evaluacion(REVISOR_A, 1, ReviewResult.REJECTED, "Corregir el contexto", 1)), politica);
        assertTrue(rechazada.allowsImprovedVersion());
        assertEquals(List.of("Corregir el contexto"), rechazada.observationsToAddress());

        ReviewHistory enSegundaRonda = ReviewHistory.of(PREGUNTA,
                List.of(asignacion(REVISOR_A, 1, AssignmentStatus.COMPLETED), asignacion(REVISOR_B, 2, AssignmentStatus.ASSIGNED)),
                List.of(evaluacion(REVISOR_A, 1, ReviewResult.REJECTED, "Corregir el contexto", 1)), politica);
        assertFalse(enSegundaRonda.allowsImprovedVersion());
        assertTrue(enSegundaRonda.observationsToAddress().isEmpty());

        ReviewHistory aprobada = ReviewHistory.of(PREGUNTA,
                List.of(asignacion(REVISOR_A, 1, AssignmentStatus.COMPLETED)),
                List.of(evaluacion(REVISOR_A, 1, ReviewResult.APPROVED, null, 1)), politica);
        assertFalse(aprobada.allowsImprovedVersion());
    }

    @Test
    @DisplayName("El historial reconoce a los revisores que participaron en cualquier ronda")
    void revisoresInvolucrados() {
        ReviewHistory historial = ReviewHistory.of(PREGUNTA,
                List.of(asignacion(REVISOR_A, 1, AssignmentStatus.COMPLETED), asignacion(REVISOR_B, 2, AssignmentStatus.ASSIGNED)),
                List.of(evaluacion(REVISOR_A, 1, ReviewResult.REJECTED, "Corregir", 1)), politica);

        assertTrue(historial.involvesReviewer(REVISOR_A));
        assertTrue(historial.involvesReviewer(REVISOR_B));
        assertFalse(historial.involvesReviewer(99L));
        assertFalse(historial.involvesReviewer(null));
        assertNotNull(historial.rounds());
    }

    @Test
    @DisplayName("El resultado de la ronda expone su etiqueta y si está cerrado")
    void resultadoDeLaRonda() {
        assertEquals("En curso", RoundOutcome.IN_PROGRESS.label());
        assertFalse(RoundOutcome.IN_PROGRESS.isClosed());
        assertTrue(RoundOutcome.APPROVED.isClosed());
        assertTrue(RoundOutcome.REJECTED.isClosed());
    }
}
