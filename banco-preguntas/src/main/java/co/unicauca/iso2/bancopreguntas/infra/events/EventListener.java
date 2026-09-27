/**
 * @file EventListener.java
 * @brief Suscriptor genérico de eventos.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.infra.events;

/**
 * @brief Suscriptor de un tipo de evento concreto.
 * @tparam T tipo de evento escuchado
 */
public interface EventListener<T> {

    /**
     * @brief Reacciona al evento publicado.
     * @param evento datos del evento
     */
    void onEvent(T evento);
}
