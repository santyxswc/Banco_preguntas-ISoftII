/**
 * @file Justificacion.java
 * @brief Objeto de Valor (Value Object) inmutable que encapsula la justificación técnica de la respuesta.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 */
package co.edu.unicauca.saberpro.domain.model;

import co.edu.unicauca.saberpro.domain.exception.ReglaNegocioException;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @brief Objeto de valor inmutable para la justificación pedagógica de la clave.
 *
 * Exige una longitud mínima de 15 caracteres, rechaza las palabras prohibidas
 * (expresiones que no argumentan la clave) y garantiza la inmutabilidad y comparación por valor.
 */
public final class Justificacion {

    /** @brief Longitud mínima de una justificación válida. */
    public static final int LONGITUD_MINIMA = 15;

    /**
     * @brief Palabras o expresiones prohibidas en una justificación (sin tildes, en minúscula).
     *
     * Son expresiones que no explican por qué la clave es correcta, sino que la dan por sentada.
     */
    public static final List<String> PALABRAS_PROHIBIDAS = List.of(
            "obvio", "obviamente", "evidentemente", "claramente",
            "porque si", "todas las anteriores", "ninguna de las anteriores");

    private final String texto;

    /**
     * @brief Construye y valida una Justificación inmutable.
     * @param texto explicación pedagógica de la clave.
     * @throws ReglaNegocioException si el texto es nulo, tiene menos de 15 caracteres
     *         o contiene alguna palabra prohibida.
     */
    public Justificacion(String texto) {
        if (texto == null || texto.trim().length() < LONGITUD_MINIMA) {
            throw new ReglaNegocioException("La justificación debe contener al menos 15 caracteres.");
        }
        String normalizado = normalizar(texto);
        for (String prohibida : PALABRAS_PROHIBIDAS) {
            if (Pattern.compile("\\b" + Pattern.quote(prohibida) + "\\b").matcher(normalizado).find()) {
                throw new ReglaNegocioException(
                        "La justificación contiene la expresión prohibida \"" + prohibida + "\".");
            }
        }
        this.texto = texto.trim();
    }

    /**
     * @brief Pasa el texto a minúsculas y le quita las tildes para comparar sin importar la escritura.
     */
    private static String normalizar(String texto) {
        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinTildes.toLowerCase(Locale.ROOT);
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
