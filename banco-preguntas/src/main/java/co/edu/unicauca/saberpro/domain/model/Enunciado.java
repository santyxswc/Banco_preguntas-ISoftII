/**
 * @file Enunciado.java
 * @brief Objeto de Valor (Value Object) inmutable que encapsula el enunciado de una pregunta.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.edu.unicauca.saberpro.domain.model;

import co.edu.unicauca.saberpro.domain.exception.ReglaNegocioException;

import java.util.Objects;

/**
 * @brief Objeto de valor inmutable para el enunciado de una pregunta Saber Pro.
 *
 * Carece de identidad propia y se define únicamente por su contenido textual.
 * Garantiza que no sea nulo ni vacío y que posea una extensión pedagógica mínima.
 */
public final class Enunciado {

    private final String texto;

    /**
     * @brief Construye y valida un Enunciado inmutable.
     * @param texto contenido textual de la pregunta.
     * @throws ReglaNegocioException si el texto es nulo, vacío o tiene menos de 10 caracteres.
     */
    public Enunciado(String texto) {
        if (texto == null || texto.trim().length() < 10) {
            throw new ReglaNegocioException("El enunciado debe contener al menos 10 caracteres significativos.");
        }
        this.texto = texto.trim();
    }

    /**
     * @brief Retorna el texto del enunciado.
     * @return texto del enunciado.
     */
    public String getTexto() {
        return texto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Enunciado enunciado = (Enunciado) o;
        return Objects.equals(texto, enunciado.texto);
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
