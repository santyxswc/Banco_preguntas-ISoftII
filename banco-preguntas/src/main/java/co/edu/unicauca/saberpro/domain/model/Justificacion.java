/**
 * @file Justificacion.java
 * @brief Objeto de Valor (Value Object) inmutable que encapsula la justificación técnica de la respuesta.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.edu.unicauca.saberpro.domain.model;

import co.edu.unicauca.saberpro.domain.exception.ReglaNegocioException;

import java.util.Objects;

/**
 * @brief Objeto de valor inmutable para la justificación pedagógica de la clave.
 *
 * Exige una longitud mínima de 15 caracteres y garantiza la inmutabilidad y comparación por valor.
 */
public final class Justificacion {

    private final String texto;

    /**
     * @brief Construye y valida una Justificación inmutable.
     * @param texto explicación pedagógica de la clave.
     * @throws ReglaNegocioException si el texto es nulo o tiene menos de 15 caracteres.
     */
    public Justificacion(String texto) {
        if (texto == null || texto.trim().length() < 15) {
            throw new ReglaNegocioException("La justificación debe contener al menos 15 caracteres.");
        }
        this.texto = texto.trim();
    }

    /**
     * @brief Obtiene el texto de la justificación.
     * @return texto de la justificación.
     */
    public String getTexto() {
        return texto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Justificacion that = (Justificacion) o;
        return Objects.equals(texto, that.texto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(texto);
    }

    @Override
    public String toString() {
        return texto;
    }
}
