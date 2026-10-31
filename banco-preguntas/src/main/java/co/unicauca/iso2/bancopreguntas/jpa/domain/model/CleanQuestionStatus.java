/**
 * @file CleanQuestionStatus.java
 * @brief Estados del ciclo de vida para la entidad limpia de dominio (Taller 10 - JPA).
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.jpa.domain.model;

/**
 * @brief Estados de la pregunta en el dominio puro, libre de anotaciones JPA.
 */
public enum CleanQuestionStatus {
    DRAFT,
    PENDING_REVIEW,
    IN_REVIEW,
    APPROVED,
    REJECTED
}
