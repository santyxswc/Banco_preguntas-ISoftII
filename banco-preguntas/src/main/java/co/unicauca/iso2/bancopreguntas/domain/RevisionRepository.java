/**
 * @file RevisionRepository.java
 * @brief Contrato de persistencia de las revisiones.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.domain;

import java.util.List;

/**
 * @brief Abstracción de persistencia para Revision.
 *
 * RevisionService depende de esta interfaz y no de una implementación
 * concreta, respetando la inversión de dependencias.
 */
public interface RevisionRepository {

    /**
     * @brief Guarda una revisión nueva y le asigna id si no tiene.
     * @param revision revisión a guardar
     * @return la revisión guardada
     */
    Revision save(Revision revision);

    /**
     * @param preguntaId id de la pregunta
     * @return revisiones de esa pregunta
     */
    List<Revision> findByPreguntaId(String preguntaId);

    /**
     * @param revisorId id del revisor
     * @return revisiones hechas por ese revisor
     */
    List<Revision> findByRevisorId(String revisorId);

    /** @return todas las revisiones registradas */
    List<Revision> list();
}
