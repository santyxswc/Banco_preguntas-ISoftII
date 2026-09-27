/**
 * @file Subject.java
 * @brief Sujeto observable del patrón Observer.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.infra;

import java.util.ArrayList;
import java.util.List;

/**
 * @brief Mantiene la lista de observadores y les avisa de los cambios.
 *
 * Solo conoce la interfaz Observer, nunca una vista concreta.
 */
public abstract class Subject {

    private final List<Observer> observadores = new ArrayList<>();

    /**
     * @brief Registra un observador (se ignoran nulos y repetidos).
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
     * @brief Quita un observador registrado.
     * @param observador observador a eliminar
     */
    public void detach(Observer observador) {

        observadores.remove(observador);
    }

    /** @brief Avisa a todos los observadores registrados. */
    protected void notifyObservers() {

        for (Observer observador : observadores) {
            observador.actualizar(this);
        }
    }
}
