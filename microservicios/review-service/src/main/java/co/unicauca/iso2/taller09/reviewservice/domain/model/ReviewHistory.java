package co.unicauca.iso2.taller09.reviewservice.domain.model;

import co.unicauca.iso2.taller09.reviewservice.domain.policy.VerdictPolicy;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Historial de revisiones de una pregunta (HU-06): todas sus rondas, de la primera a la última.
 * <p>
 * Es el modelo de lectura que el autor consulta para conocer las decisiones y observaciones
 * de cada revisor y decidir si saca una versión mejorada. Se reconstruye en el dominio a
 * partir de las asignaciones y las evaluaciones, sin depender de la persistencia.
 * <p>
 * Regla de negocio: solo se puede sacar una versión mejorada cuando la última ronda está
 * cerrada y fue rechazada.
 */
public final class ReviewHistory {

    private final Long questionId;
    private final List<ReviewRound> rounds;

    private ReviewHistory(Long questionId, List<ReviewRound> rounds) {
        this.questionId = Objects.requireNonNull(questionId, "questionId");
        this.rounds = List.copyOf(rounds);
    }

    /** Agrupa asignaciones y evaluaciones por número de ronda y ordena las rondas de menor a mayor. */
    public static ReviewHistory of(Long questionId, List<ReviewAssignment> assignments, List<Review> reviews,
                                   VerdictPolicy policy) {
        Map<Integer, List<ReviewAssignment>> assignmentsByRound = (assignments == null ? List.<ReviewAssignment>of() : assignments)
                .stream().collect(Collectors.groupingBy(ReviewAssignment::getRound, TreeMap::new, Collectors.toList()));
        Map<Integer, List<Review>> reviewsByRound = (reviews == null ? List.<Review>of() : reviews)
                .stream().collect(Collectors.groupingBy(Review::getRound));
        List<ReviewRound> rounds = assignmentsByRound.entrySet().stream()
                .map(entry -> ReviewRound.of(entry.getKey(), entry.getValue(),
                        reviewsByRound.getOrDefault(entry.getKey(), List.of()), policy))
                .toList();
        return new ReviewHistory(questionId, rounds);
    }

    public Long questionId() {
        return questionId;
    }

    public List<ReviewRound> rounds() {
        return rounds;
    }

    public int totalRounds() {
        return rounds.size();
    }

    public boolean isEmpty() {
        return rounds.isEmpty();
    }

    public Optional<ReviewRound> latestRound() {
        return rounds.isEmpty() ? Optional.empty() : Optional.of(rounds.get(rounds.size() - 1));
    }

    /** Una versión mejorada solo tiene sentido si la última ronda terminó en rechazo. */
    public boolean allowsImprovedVersion() {
        return latestRound().map(ReviewRound::isRejected).orElse(false);
    }

    /** Observaciones de la última ronda rechazada: lo que el autor debe corregir en la nueva versión. */
    public List<String> observationsToAddress() {
        return latestRound().filter(ReviewRound::isRejected).map(ReviewRound::observations).orElse(List.of());
    }

    /** Indica si el usuario fue revisor de la pregunta en alguna de sus rondas. */
    public boolean involvesReviewer(Long reviewerId) {
        return rounds.stream().anyMatch(round -> round.involves(reviewerId));
    }
}
