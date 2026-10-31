/**
 * @file DomainException.java
 * @brief Excepción base para el dominio de Saber Pro (DDD).
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.edu.unicauca.saberpro.domain.exception;

/**
 * @brief Excepción base no verificada para el modelo de dominio DDD.
 *
 * Se utiliza como raíz para encapsular fallos semánticos o violaciones de integridad
 * dentro del contexto delimitado de autoría y preguntas Saber Pro.
 */
public class DomainException extends RuntimeException {

    /**
     * @brief Construye una nueva excepción de dominio con el mensaje especificado.
     * @param mensaje detalle de la causa de la excepción.
     */
    public DomainException(String mensaje) {
        super(mensaje);
    }

    /**
     * @brief Construye una nueva excepción de dominio con mensaje y causa raíz.
     * @param mensaje detalle del fallo.
     * @param causa excepción original.
     */
    public DomainException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
