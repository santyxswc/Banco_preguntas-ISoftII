/**
 * @file EventBus.java
 * @brief Patrón GoF Creacional: Singleton para el bus global de eventos.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.infra.events;

/**
 * @brief Bus centralizado de eventos del sistema (Patrón Singleton).
 *
 * Garantiza una única instancia accesible globalmente para publicar y
 * suscribirse a eventos de dominio en una arquitectura orientada a eventos.
 */
public final class EventBus {

    private static volatile EventBus instance;
    private final PreguntaEventPublisher publisher;

    private EventBus() {
        this.publisher = new PreguntaEventPublisher();
    }

    /**
     * @brief Retorna la instancia única del EventBus (Double-Checked Locking).
     * @return instancia singleton de EventBus
     */
    public static EventBus getInstance() {
        if (instance == null) {
            synchronized (EventBus.class) {
                if (instance == null) {
                    instance = new EventBus();
                }
            }
        }
        return instance;
    }

    /**
     * @brief Reinicia la instancia (útil para pruebas unitarias).
     */
    public static synchronized void resetInstance() {
        instance = null;
    }

    public PreguntaEventPublisher getPublisher() {
        return publisher;
    }

    public void publish(RevisorAsignadoEvent event) {
        publisher.publish(event);
    }

    public void publishRevision(RevisionCompletadaEvent event) {
        publisher.publishRevision(event);
    }

    public void subscribe(EventListener<RevisorAsignadoEvent> listener) {
        publisher.subscribe(listener);
    }

    public void subscribeRevision(EventListener<RevisionCompletadaEvent> listener) {
        publisher.subscribeRevision(listener);
    }
}
