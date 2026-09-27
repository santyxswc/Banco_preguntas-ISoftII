/**
 * @file Observer.java
 * @brief Interfaz observador del patrón Observer.
 * @author Santiago Caicedo
 */
package co.unicauca.iso2.bancopreguntas.infra;

/**
 * @brief Objeto que quiere enterarse de los cambios de un Subject.
 *
 * Las vistas que se refrescan solas (estadísticas, gráfica, listado,
 * asignación) implementan esta interfaz.
 */
public interface Observer {

    /**
     * @brief Se llama cada vez que el sujeto cambia.
     * @param subject sujeto que notifica el cambio
     */
    void actualizar(Subject subject);
}
