package co.unicauca.iso2.taller09.reviewservice;

import co.unicauca.iso2.taller09.common.enums.AssignmentStatus;
import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import co.unicauca.iso2.taller09.common.enums.ReviewResult;
import co.unicauca.iso2.taller09.reviewservice.domain.exception.ReviewException;
import co.unicauca.iso2.taller09.reviewservice.domain.model.PageResult;
import co.unicauca.iso2.taller09.reviewservice.domain.model.Review;
import co.unicauca.iso2.taller09.reviewservice.domain.model.ReviewAssignment;
import co.unicauca.iso2.taller09.reviewservice.domain.model.ReviewerProfile;
import co.unicauca.iso2.taller09.reviewservice.domain.model.ReviewerSelection;
import co.unicauca.iso2.taller09.reviewservice.domain.policy.UnanimousApprovalPolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReviewDomainTest {

    @Test
    @DisplayName("Una asignación nueva está pendiente y solo se completa una vez")
    void asignacion() {
        ReviewAssignment a = ReviewAssignment.create(1L, 4L, 1L, 1);
        assertEquals(1, a.getRound());
        assertTrue(a.isPending());
        a.complete();
        assertEquals(AssignmentStatus.COMPLETED, a.getStatus());
        assertThrows(ReviewException.class, a::complete);
        assertThrows(ReviewException.class, () -> ReviewAssignment.create(1L, 4L, 1L, 0));
    }

    @Test
    @DisplayName("La aprobación admite observaciones vacías y el rechazo no")
    void reglasDeRevision() {
        Review aprobada = Review.register(1L, 4L, 1, ReviewResult.APPROVED, "  ");
        assertTrue(aprobada.isApproved());
        assertNull(aprobada.getComments());

        Review rechazada = Review.register(1L, 4L, 1, ReviewResult.REJECTED, "  Falta bibliografía ");
        assertEquals("Falta bibliografía", rechazada.getComments());

        ReviewException e = assertThrows(ReviewException.class,
                () -> Review.register(1L, 4L, 1, ReviewResult.REJECTED, null));
        assertEquals(ReviewException.Type.VALIDATION, e.getType());
    }

    @Test
    @DisplayName("Solo REVISOR o DOCENTE pueden revisar")
    void perfilRevisor() {
        assertTrue(new ReviewerProfile(4L, "a", "a@x", List.of("revisor")).canReview());
        assertTrue(new ReviewerProfile(3L, "b", "b@x", List.of("AUTOR", "DOCENTE")).canReview());
        assertFalse(new ReviewerProfile(6L, "c", "c@x", List.of("ESTUDIANTE")).canReview());
        assertFalse(new ReviewerProfile(7L, "d", "d@x", null).canReview());
    }

    @Test
    @DisplayName("La política unánime aprueba solo si todos aprueban")
    void politicaUnanime() {
        UnanimousApprovalPolicy policy = new UnanimousApprovalPolicy();
        Review si = Review.register(1L, 4L, 1, ReviewResult.APPROVED, null);
        Review no = Review.register(1L, 5L, 1, ReviewResult.REJECTED, "x");
        assertEquals(QuestionStatus.APPROVED, policy.decide(List.of(si, si)));
        assertEquals(QuestionStatus.REJECTED, policy.decide(List.of(si, no)));
        assertThrows(ReviewException.class, () -> policy.decide(List.of()));
    }

    @Test
    @DisplayName("PageResult calcula el total de páginas")
    void pagina() {
        assertEquals(3, new PageResult<>(List.of(1, 2), 0, 2, 5).totalPages());
        assertEquals(0, new PageResult<>(List.of(), 0, 0, 0).totalPages());
    }

    @Test
    @DisplayName("PageResult.map transforma el contenido conservando la paginación")
    void paginaSeTransforma() {
        PageResult<ReviewAssignment> origen = new PageResult<>(
                List.of(ReviewAssignment.create(1L, 4L, 1L, 1), ReviewAssignment.create(1L, 5L, 1L, 1)), 0, 2, 5);

        PageResult<Long> revisores = origen.map(ReviewAssignment::getReviewerId);

        assertEquals(List.of(4L, 5L), revisores.content());
        assertEquals(0, revisores.page());
        assertEquals(2, revisores.size());
        assertEquals(5, revisores.totalElements());
        assertEquals(3, revisores.totalPages());
    }

    @Test
    @DisplayName("Una asignación creada nace ASSIGNED con su ronda y auditoría")
    void asignacionCreada() {
        ReviewAssignment a = ReviewAssignment.create(7L, 8L, 9L, 2);

        assertEquals(AssignmentStatus.ASSIGNED, a.getStatus());
        assertEquals(7L, a.getQuestionId());
        assertEquals(8L, a.getReviewerId());
        assertEquals(9L, a.getAssignedBy());
        assertEquals(2, a.getRound());
        assertNotNull(a.getAssignedAt());
    }

    @Test
    @DisplayName("Review.register exige la decisión y conserva la ronda")
    void revisionRegistrada() {
        Review review = Review.register(1L, 4L, 2, ReviewResult.APPROVED, null);

        assertEquals(2, review.getRound());
        assertEquals(ReviewResult.APPROVED, review.getResult());
        assertNotNull(review.getCreatedAt());
        ReviewException e = assertThrows(ReviewException.class,
                () -> Review.register(1L, 4L, 2, null, "x"));
        assertEquals(ReviewException.Type.VALIDATION, e.getType());
    }

    @Test
    @DisplayName("ReviewerSelection admite de 1 a 3 revisores y descarta identidades nulas")
    void seleccionValida() {
        assertEquals(List.of(4L), ReviewerSelection.of(List.of(4L)).reviewerIds());
        assertEquals(List.of(4L, 5L, 3L), ReviewerSelection.of(List.of(4L, 5L, 3L)).reviewerIds());
        assertEquals(List.of(4L), ReviewerSelection.of(Arrays.asList(4L, null)).reviewerIds());
    }

    @Test
    @DisplayName("ReviewerSelection rechaza listas vacías, más de tres y revisores repetidos")
    void seleccionInvalida() {
        assertEquals(ReviewException.Type.VALIDATION,
                assertThrows(ReviewException.class, () -> ReviewerSelection.of(List.of())).getType());
        assertEquals(ReviewException.Type.VALIDATION,
                assertThrows(ReviewException.class, () -> ReviewerSelection.of(null)).getType());
        assertEquals(ReviewException.Type.VALIDATION,
                assertThrows(ReviewException.class, () -> ReviewerSelection.of(List.of(1L, 2L, 3L, 4L))).getType());
        assertEquals(ReviewException.Type.VALIDATION,
                assertThrows(ReviewException.class, () -> ReviewerSelection.of(Arrays.asList(1L, 1L))).getType());
    }
}
