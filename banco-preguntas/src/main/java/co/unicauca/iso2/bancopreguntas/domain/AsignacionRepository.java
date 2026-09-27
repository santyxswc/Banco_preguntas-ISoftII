/**
 * @file AsignacionRepository.java
 * @brief Contrato de persistencia de las asignaciones de revisores.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.domain;

import java.util.List;

/**
 * @brief Abstracción de persistencia para Asignacion.
 */
public interface AsignacionRepository {

    /**
     * @brief Guarda una asignación nueva y le asigna id si no tiene.
     * @param asignacion asignación a guardar
     * @return la asignación guardada
     */
    Asignacion save(Asignacion asignacion);

    /**
     * @brief Historial de asignaciones de una pregunta.
     * @param preguntaId id de la pregunta
     * @return asignaciones de esa pregunta
     */
    List<Asignacion> findByPreguntaId(String preguntaId);

    /** @return todas las asignaciones registradas */
    List<Asignacion> list();
}
