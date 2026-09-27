/**
 * @file RevisorAsignadoEvent.java
 * @brief Evento de asignación de revisores.
 * @author Santiago Caicedo
 */
package co.unicauca.iso2.bancopreguntas.infra.events;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @brief Se publica cuando el administrador asigna revisores a una
 *        pregunta.
 */
public final class RevisorAsignadoEvent {

    private final String preguntaId;
    private final List<String> revisorIds;
    private final LocalDateTime fecha;

    public RevisorAsignadoEvent(String preguntaId, List<String> revisorIds,
                                 LocalDateTime fecha) {
        this.preguntaId = preguntaId;
        this.revisorIds = revisorIds != null
                ? Collections.unmodifiableList(new ArrayList<>(revisorIds))
                : Collections.emptyList();
        this.fecha = fecha;
    }

    public String getPreguntaId() {
        return preguntaId;
    }

    public List<String> getRevisorIds() {
        return revisorIds;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}
