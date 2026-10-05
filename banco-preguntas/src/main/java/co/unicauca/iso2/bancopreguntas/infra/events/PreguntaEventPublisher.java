/**
 * @file PreguntaEventPublisher.java
 * @brief Publicador de eventos de preguntas.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.infra.events;

import java.util.ArrayList;
import java.util.List;

/**
 * @brief Publica RevisorAsignadoEvent a los suscriptores registrados.
 *
 * Si un suscriptor falla (por ejemplo el correo), el error se registra
 * y los demás suscriptores se siguen ejecutando.
 */
public class PreguntaEventPublisher {

    private final List<EventListener<RevisorAsignadoEvent>> listenersRevisorAsignado =
            new ArrayList<>();
    private final List<EventListener<RevisionCompletadaEvent>> listenersRevisionCompletada =
            new ArrayList<>();

    /** @param listener suscriptor a registrar */
    public void subscribe(EventListener<RevisorAsignadoEvent> listener) {
        if (listener != null) {
            listenersRevisorAsignado.add(listener);
        }
    }

    /** @param listener suscriptor a quitar */
    public void unsubscribe(EventListener<RevisorAsignadoEvent> listener) {
        listenersRevisorAsignado.remove(listener);
    }

    /** @param listener suscriptor para revisiones completadas */
    public void subscribeRevision(EventListener<RevisionCompletadaEvent> listener) {
        if (listener != null) {
            listenersRevisionCompletada.add(listener);
        }
    }

    /** @param listener suscriptor de revisiones a quitar */
    public void unsubscribeRevision(EventListener<RevisionCompletadaEvent> listener) {
        listenersRevisionCompletada.remove(listener);
    }

    /**
     * @brief Entrega el evento a cada suscriptor de asignación.
     * @param evento evento a publicar
     */
    public void publish(RevisorAsignadoEvent evento) {
        if (evento == null) {
            return;
        }
        for (EventListener<RevisorAsignadoEvent> listener : listenersRevisorAsignado) {
            try {
                listener.onEvent(evento);
            } catch (RuntimeException e) {
                System.err.println("[EVENTO-ERROR] Un listener de "
                        + "RevisorAsignadoEvent falló: " + e.getMessage());
            }
        }
    }

    /**
     * @brief Entrega el evento a cada suscriptor de revisión completada.
     * @param evento evento de revisión a publicar
     */
    public void publishRevision(RevisionCompletadaEvent evento) {
        if (evento == null) {
            return;
        }
        for (EventListener<RevisionCompletadaEvent> listener : listenersRevisionCompletada) {
            try {
                listener.onEvent(evento);
            } catch (RuntimeException e) {
                System.err.println("[EVENTO-ERROR] Un listener de "
                        + "RevisionCompletadaEvent falló: " + e.getMessage());
            }
        }
    }
}
