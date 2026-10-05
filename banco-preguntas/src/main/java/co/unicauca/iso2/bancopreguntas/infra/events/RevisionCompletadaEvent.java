/**
 * @file RevisionCompletadaEvent.java
 * @brief Evento publicado cuando un revisor completa la evaluación de una pregunta (HU05).
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.infra.events;

import co.unicauca.iso2.bancopreguntas.domain.EstadoPregunta;

import java.time.LocalDateTime;

/**
 * @brief Se publica cuando un revisor aprueba o rechaza una pregunta.
 *
 * Contiene los datos necesarios para notificar al autor sobre el
 * resultado y las observaciones.
 */
public final class RevisionCompletadaEvent {

    private final String preguntaId;
    private final String revisorId;
    private final EstadoPregunta veredicto;
    private final String observaciones;
    private final LocalDateTime fecha;

    public RevisionCompletadaEvent(String preguntaId, String revisorId,
                                  EstadoPregunta veredicto, String observaciones,
                                  LocalDateTime fecha) {
        this.preguntaId = preguntaId;
        this.revisorId = revisorId;
        this.veredicto = veredicto;
        this.observaciones = observaciones;
        this.fecha = fecha != null ? fecha : LocalDateTime.now();
    }

    public String getPreguntaId() {
        return preguntaId;
    }

    public String getRevisorId() {
        return revisorId;
    }

    public EstadoPregunta getVeredicto() {
        return veredicto;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}
