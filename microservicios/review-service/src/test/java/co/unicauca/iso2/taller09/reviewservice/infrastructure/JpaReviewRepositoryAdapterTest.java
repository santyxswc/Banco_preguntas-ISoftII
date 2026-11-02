package co.unicauca.iso2.taller09.reviewservice.infrastructure;

import co.unicauca.iso2.taller09.common.enums.AssignmentStatus;
import co.unicauca.iso2.taller09.common.enums.ReviewResult;
import co.unicauca.iso2.taller09.reviewservice.domain.model.PageResult;
import co.unicauca.iso2.taller09.reviewservice.domain.model.Review;
import co.unicauca.iso2.taller09.reviewservice.domain.model.ReviewAssignment;
import co.unicauca.iso2.taller09.reviewservice.infrastructure.persistence.JpaReviewRepositoryAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import(JpaReviewRepositoryAdapter.class)
class JpaReviewRepositoryAdapterTest {

    @Autowired
    private JpaReviewRepositoryAdapter repository;

    @Test
    @DisplayName("Guarda una asignación y la completa sin duplicarla")
    void guardaYCompletaAsignacion() {
        ReviewAssignment saved = repository.saveAssignment(ReviewAssignment.create(100L, 4L, 1L, 1));
        assertNotNull(saved.getId());

        ReviewAssignment leida = repository.findLatestAssignment(100L, 4L).orElseThrow();
        assertTrue(leida.isPending());
        leida.complete();
        repository.saveAssignment(leida);

        assertEquals(1, repository.findAllAssignments().size());
        assertEquals(AssignmentStatus.COMPLETED, repository.findLatestAssignment(100L, 4L).orElseThrow().getStatus());
    }

    @Test
    @DisplayName("Lista paginadas solo las asignaciones pendientes del revisor")
    void paginaPendientesDelRevisor() {
        repository.saveAssignment(ReviewAssignment.create(100L, 4L, 1L, 1));
        repository.saveAssignment(ReviewAssignment.create(101L, 4L, 1L, 1));
        repository.saveAssignment(ReviewAssignment.create(102L, 4L, 1L, 1));
        repository.saveAssignment(ReviewAssignment.create(103L, 5L, 1L, 1));
        ReviewAssignment completada = repository.saveAssignment(ReviewAssignment.create(104L, 4L, 1L, 1));
        completada.complete();
        repository.saveAssignment(completada);

        PageResult<ReviewAssignment> pagina0 = repository.findPendingByReviewer(4L, 0, 2);
        PageResult<ReviewAssignment> pagina1 = repository.findPendingByReviewer(4L, 1, 2);

        assertEquals(3, pagina0.totalElements());
        assertEquals(2, pagina0.totalPages());
        assertEquals(2, pagina0.content().size());
        assertEquals(1, pagina1.content().size());
        assertTrue(pagina0.content().stream().allMatch(a -> a.getReviewerId().equals(4L) && a.isPending()));
    }

    @Test
    @DisplayName("En una nueva ronda devuelve la asignación más reciente del revisor")
    void ultimaAsignacionPorRonda() {
        ReviewAssignment ronda1 = repository.saveAssignment(ReviewAssignment.create(200L, 4L, 1L, 1));
        ronda1.complete();
        repository.saveAssignment(ronda1);
        repository.saveAssignment(ReviewAssignment.create(200L, 4L, 1L, 2));

        ReviewAssignment ultima = repository.findLatestAssignment(200L, 4L).orElseThrow();
        assertEquals(2, ultima.getRound());
        assertTrue(ultima.isPending());
        assertEquals(2, repository.findAssignmentsByQuestion(200L).size());
        assertFalse(repository.findLatestAssignment(200L, 9L).isPresent());
    }

    @Test
    @DisplayName("Guarda las revisiones con sus observaciones y las devuelve en orden")
    void guardaRevisiones() {
        repository.saveReview(Review.register(300L, 4L, 1, ReviewResult.REJECTED, "La opción C delata la clave"));
        repository.saveReview(Review.register(300L, 5L, 1, ReviewResult.APPROVED, null));
        repository.saveReview(Review.register(301L, 4L, 1, ReviewResult.APPROVED, null));

        List<Review> revisiones = repository.findReviewsByQuestion(300L);
        assertEquals(2, revisiones.size());
        assertEquals("La opción C delata la clave", revisiones.get(0).getComments());
        assertEquals(ReviewResult.REJECTED, revisiones.get(0).getResult());
        assertEquals(1, revisiones.get(0).getRound());
    }
}
