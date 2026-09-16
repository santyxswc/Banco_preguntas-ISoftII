package co.unicauca.iso2.bancopreguntas.infra;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SubjectTest {

    /**
     * Subject concreto usado únicamente para poder probar la clase
     * abstracta: expone notifyObservers() como público.
     */
    private static class SubjectDePrueba extends Subject {
        void dispararNotificacion() {
            notifyObservers();
        }
    }

    @Test
    void attachRegistraUnObservadorQueRecibeNotificaciones() {

        SubjectDePrueba subject = new SubjectDePrueba();

        int[] contador = {0};
        subject.attach(s -> contador[0]++);

        subject.dispararNotificacion();
        subject.dispararNotificacion();

        assertEquals(2, contador[0]);
    }

    @Test
    void detachEvitaQueUnObservadorSigaSiendoNotificado() {

        SubjectDePrueba subject = new SubjectDePrueba();

        int[] contador = {0};
        Observer observador = s -> contador[0]++;

        subject.attach(observador);
        subject.detach(observador);

        subject.dispararNotificacion();

        assertEquals(0, contador[0]);
    }

    @Test
    void attachNoRegistraElMismoObservadorDosVeces() {

        SubjectDePrueba subject = new SubjectDePrueba();

        int[] contador = {0};
        Observer observador = s -> contador[0]++;

        subject.attach(observador);
        subject.attach(observador);

        subject.dispararNotificacion();

        assertEquals(1, contador[0]);
    }

    @Test
    void attachIgnoraObservadoresNulos() {

        SubjectDePrueba subject = new SubjectDePrueba();

        assertFalse(hayError(() -> subject.attach(null)));

        subject.dispararNotificacion();

        assertTrue(true);
    }

    private boolean hayError(Runnable accion) {
        try {
            accion.run();
            return false;
        } catch (Exception ex) {
            return true;
        }
    }
}
