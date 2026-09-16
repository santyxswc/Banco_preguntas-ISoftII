package co.unicauca.iso2.bancopreguntas.infra;

import java.util.ArrayList;
import java.util.List;

/**
 * Parte del patrón Observer.
 *
 * Es lógica transversal reutilizable por cualquier clase del dominio
 * que necesite notificar a varias vistas pendientes de sus cambios,
 * sin acoplarse a ninguna de ellas: Subject solo conoce la interfaz
 * {@link Observer}, nunca una vista concreta.
 *
 * En esta aplicación, QuestionService extiende Subject: cada vez que
 * cambia el estado de una pregunta, notifica a los observadores
 * registrados (GUIObserver1 y GUIObserver2) para que se rendericen
 * de nuevo.
 */
public abstract class Subject {

    private final List<Observer> observadores = new ArrayList<>();

    /**
     * Registra un observador para que sea notificado de los cambios
     * de este sujeto.
     *
     * @param observador observador a registrar
     */
    public void attach(Observer observador) {

        if (observador == null) {
            return;
        }

        if (!observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    /**
     * Elimina un observador previamente registrado.
     *
     * @param observador observador a eliminar
     */
    public void detach(Observer observador) {

        observadores.remove(observador);
    }

    /**
     * Notifica a todos los observadores registrados de que el estado
     * de este sujeto cambió.
     *
     * Solo las subclases (por ejemplo, QuestionService) pueden
     * disparar la notificación, típicamente al final de una
     * operación que modifica su estado.
     */
    protected void notifyObservers() {

        for (Observer observador : observadores) {
            observador.actualizar(this);
        }
    }
}
