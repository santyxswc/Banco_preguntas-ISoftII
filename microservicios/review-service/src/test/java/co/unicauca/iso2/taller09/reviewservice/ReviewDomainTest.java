package co.unicauca.iso2.taller09.reviewservice;

import co.unicauca.iso2.taller09.common.enums.AssignmentStatus;
import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import co.unicauca.iso2.taller09.common.enums.ReviewResult;
import co.unicauca.iso2.taller09.reviewservice.domain.exception.ReviewException;
import co.unicauca.iso2.taller09.reviewservice.domain.model.PageResult;
import co.unicauca.iso2.taller09.reviewservice.domain.model.Review;
import co.unicauca.iso2.taller09.reviewservice.domain.model.ReviewAssignment;
import co.unicauca.iso2.taller09.reviewservice.domain.model.ReviewerProfile;
import co.unicauca.iso2.taller09.reviewservice.domain.policy.UnanimousApprovalPolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
}
