package co.unicauca.iso2.taller09.reviewservice.domain.model;

import co.unicauca.iso2.taller09.reviewservice.domain.exception.ReviewException;

import java.util.List;
import java.util.Objects;

/**
 * Objeto de Valor que representa el conjunto de revisores propuesto para una pregunta.
 * <p>
 * Encapsula la invariante de asignación del contexto de revisión: entre 1 y 3 revisores,
 * sin identidades repetidas. Los identificadores nulos se descartan.
 */
public record ReviewerSelection(List<Long> reviewerIds) {

    public static final int MIN_REVISORES = 1;
    public static final int MAX_REVISORES = 3;

    public ReviewerSelection {
        List<Long> solicitados = reviewerIds == null ? List.of()
                : reviewerIds.stream().filter(Objects::nonNull).toList();
        List<Long> distintos = solicitados.stream().distinct().toList();
        if (distintos.size() < MIN_REVISORES || distintos.size() > MAX_REVISORES) {
            throw ReviewException.validation("Debe asignar entre " + MIN_REVISORES + " y " + MAX_REVISORES + " revisores.");
        }
        if (distintos.size() != solicitados.size()) {
            throw ReviewException.validation("No se puede asignar el mismo revisor más de una vez.");
        }
        reviewerIds = List.copyOf(distintos);
    }

    public static ReviewerSelection of(List<Long> reviewerIds) {
        return new ReviewerSelection(reviewerIds);
    }
}
