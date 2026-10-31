/**
 * @file ReglaNegocioException.java
 * @brief Excepción de violación de invariantes y reglas del negocio en el dominio.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.edu.unicauca.saberpro.domain.exception;

/**
 * @brief Excepción lanzada cuando se intenta romper una invariante de negocio.
 *
 * Ejemplos: intentar agregar más de 4 opciones a una pregunta, enviar a revisión
 * sin exactamente 4 opciones o sin 1 respuesta correcta, justificación menor a 15 caracteres, etc.
 */
public class ReglaNegocioException extends DomainException {

    /**
     * @brief Constructor con el mensaje explicativo de la regla de negocio infringida.
     * @param mensaje descripción del motivo del rechazo.
     */
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
