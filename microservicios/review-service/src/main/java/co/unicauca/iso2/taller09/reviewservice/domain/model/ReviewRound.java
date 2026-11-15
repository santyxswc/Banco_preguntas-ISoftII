package co.unicauca.iso2.taller09.reviewservice.domain.model;

import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import co.unicauca.iso2.taller09.reviewservice.domain.exception.ReviewException;
import co.unicauca.iso2.taller09.reviewservice.domain.policy.VerdictPolicy;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Objeto de Valor: una ronda de revisión por pares de una pregunta (HU-06).
 * <p>
 * Agrupa las asignaciones y las evaluaciones que comparten el mismo número de ronda y deriva
 * de ellas el resultado. Cada vez que el autor reenvía una versión mejorada de una pregunta
 * rechazada y el administrador le asigna revisores, se abre una ronda nueva (ronda + 1).
 * <p>
 * Invariantes: la ronda es mayor o igual a 1, tiene al menos una asignación y solo queda
 * cerrada cuando todos los revisores asignados registraron su decisión.
 */
public record ReviewRound(int number, List<ReviewAssignment> assignments, List<Review> reviews, RoundOutcome outcome) {

    public ReviewRound {
        if (number < 1) {
            throw ReviewException.validation("La ronda de revisión debe ser mayor o igual a 1.");
        }
        if (assignments == null || assignments.isEmpty()) {
            throw ReviewException.validation("Una ronda de revisión debe tener al menos un revisor asignado.");
        }
        Objects.requireNonNull(outcome, "outcome");
        assignments = List.copyOf(assignments);
        reviews = reviews == null ? List.of() : List.copyOf(reviews);
    }

    /**
     * Arma la ronda y calcula su resultado: en curso si falta alguna evaluación; si no, el que
     * decida la política de veredicto (por defecto, aprobación unánime).
     */
    public static ReviewRound of(int number, List<ReviewAssignment> assignments, List<Review> reviews,
                                 VerdictPolicy policy) {
        Objects.requireNonNull(policy, "policy");
        List<Review> ordered = reviews == null ? List.of() : reviews.stream()
                .sorted(Comparator.comparing(Review::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
        boolean pending = assignments != null && assignments.stream().anyMatch(ReviewAssignment::isPending);
        RoundOutcome outcome;
        if (pending || ordered.isEmpty()) {
            outcome = RoundOutcome.IN_PROGRESS;
        } else {
            outcome = policy.decide(ordered) == QuestionStatus.APPROVED ? RoundOutcome.APPROVED : RoundOutcome.REJECTED;
        }
        return new ReviewRound(number, assignments, ordered, outcome);
    }

    public boolean isClosed() {
        return outcome.isClosed();
    }

    public boolean isRejected() {
        return outcome == RoundOutcome.REJECTED;
    }

    /** Revisores asignados a la ronda, en el orden en que se asignaron. */
    public List<Long> reviewerIds() {
        return assignments.stream().map(ReviewAssignment::getReviewerId).toList();
    }

    /** Revisores que todavía no registran su decisión. */
    public List<Long> pendingReviewerIds() {
        return assignments.stream().filter(ReviewAssignment::isPending).map(ReviewAssignment::getReviewerId).toList();
    }

    public boolean involves(Long reviewerId) {
        return reviewerId != null && reviewerIds().contains(reviewerId);
    }

    /** Observaciones escritas por los revisores en esta ronda (se omiten las evaluaciones sin texto). */
    public List<String> observations() {
        return reviews.stream().map(Review::getComments).filter(Objects::nonNull).toList();
    }

    public LocalDateTime startedAt() {
        return assignments.stream().map(ReviewAssignment::getAssignedAt).filter(Objects::nonNull)
                .min(Comparator.naturalOrder()).orElse(null);
    }

    /** Fecha de la última evaluación; {@code null} mientras la ronda siga en curso. */
    public LocalDateTime closedAt() {
        if (!isClosed()) {
            return null;
        }
        return reviews.stream().map(Review::getCreatedAt).filter(Objects::nonNull)
                .max(Comparator.naturalOrder()).orElse(null);
    }
}
