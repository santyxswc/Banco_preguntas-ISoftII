/**
 * @file OpcionRespuesta.java
 * @brief Objeto de Valor (Value Object) inmutable que encapsula una opción de respuesta (distractor o clave).
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 */
package co.edu.unicauca.saberpro.domain.model;

import co.edu.unicauca.saberpro.domain.exception.ReglaNegocioException;

import java.util.Objects;

/**
 * @brief Representa una opción de respuesta inmutable para preguntas Saber Pro.
 *
 * Contiene el texto de la opción y un indicador booleano de si corresponde a la clave correcta.
 */
public final class OpcionRespuesta {

    private final String texto;
    private final boolean esCorrecta;

    /**
     * @brief Constructor que valida e inicializa una opción de respuesta.
     * @param texto contenido de la opción.
     * @param esCorrecta true si es la clave correcta, false si es distractor.
     * @throws ReglaNegocioException si el texto es nulo o está vacío.
     */
    public OpcionRespuesta(String texto, boolean esCorrecta) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new ReglaNegocioException("El texto de la opción de respuesta no puede estar vacío.");
        }
        this.texto = texto.trim();
        this.esCorrecta = esCorrecta;
    }

    /**
     * @brief Retorna el texto descriptivo de la opción.
     * @return texto de la opción.
     */
    public String getTexto() {
        return texto;
    }

    /**
     * @brief Indica si la opción es la clave de respuesta correcta.
     * @return true si es correcta, false en caso contrario.
     */
    public boolean isEsCorrecta() {
        return esCorrecta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpcionRespuesta that = (OpcionRespuesta) o;
        return esCorrecta == that.esCorrecta && Objects.equals(texto, that.texto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(texto, esCorrecta);
    }

    @Override
    public String toString() {
        return texto + (esCorrecta ? " [CORRECTA]" : " [DISTRACTOR]");
    }
}
