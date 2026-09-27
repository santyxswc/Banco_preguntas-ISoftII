/**
 * @file PreguntaEventPublisher.java
 * @brief Publicador de eventos de preguntas.
 * @author Santiago Caicedo
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

    /**
     * @brief Entrega el evento a cada suscriptor.
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
}
