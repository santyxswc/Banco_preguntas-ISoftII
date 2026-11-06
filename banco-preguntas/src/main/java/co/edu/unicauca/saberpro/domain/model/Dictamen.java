/**
 * @file Dictamen.java
 * @brief Enumeración del veredicto que emite un revisor sobre una pregunta.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 */
package co.edu.unicauca.saberpro.domain.model;

/**
 * @brief Veredicto de una revisión por pares.
 */
public enum Dictamen {
    /** @brief La pregunta cumple los criterios pedagógicos y puede aprobarse. */
    APROBADA,
    /** @brief La pregunta no cumple; el revisor debe dejar una observación. */
    RECHAZADA
}
