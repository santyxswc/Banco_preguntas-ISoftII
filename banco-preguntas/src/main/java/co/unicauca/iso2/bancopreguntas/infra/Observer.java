package co.unicauca.iso2.bancopreguntas.infra;

/**
 * Parte del patrón Observer.
 *
 * Cualquier vista que deba reaccionar a un cambio de estado de un
 * {@link Subject} debe implementar esta interfaz y registrarse en él
 * mediante {@link Subject#attach(Observer)}.
 *
 * En esta aplicación, las vistas GUIObserver1 (estadísticas) y
 * GUIObserver2 (gráfica de pastel) implementan esta interfaz para
 * enterarse de los cambios de estado de las preguntas del banco.
 */
public interface Observer {

    /**
     * Método invocado por el sujeto observado cada vez que su estado
     * cambia.
     *
     * @param subject sujeto que notifica el cambio
     */
    void actualizar(Subject subject);
}
