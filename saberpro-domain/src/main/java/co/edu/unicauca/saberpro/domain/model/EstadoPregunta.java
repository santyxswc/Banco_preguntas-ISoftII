/**
 * @file EstadoPregunta.java
 * @brief Ciclo de vida y estados de una pregunta en el modelado táctico DDD.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 */
package co.edu.unicauca.saberpro.domain.model;

/**
 * @brief Enum que representa los estados controlados del Agregado Pregunta.
 *
 * El ciclo de vida comienza en BORRADOR y transiciona a PENDIENTE_REVISION,
 * EN_REVISION, APROBADA o RECHAZADA respetando las invariantes del negocio.
 */
public enum EstadoPregunta {

    /** @brief Estado inicial de redacción y edición de la pregunta. */
    BORRADOR,

    /** @brief Pregunta completa validada y lista para asignación de revisores pares. */
    PENDIENTE_REVISION,

    /** @brief Pregunta en proceso de evaluación por parte de docentes revisores. */
    EN_REVISION,

    /** @brief Pregunta aprobada y certificada para su publicación en pruebas Saber Pro. */
    APROBADA,

    /** @brief Pregunta rechazada por inconsistencias pedagógicas o estructurales. */
    RECHAZADA
}
