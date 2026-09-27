/**
 * @file NivelDificultad.java
 * @brief Niveles de dificultad válidos para una pregunta.
 * @author Santiago Caicedo
 */
package co.unicauca.iso2.bancopreguntas.domain;

/**
 * @brief Catálogo cerrado de niveles de dificultad.
 */
public enum NivelDificultad {

    BASICO("Básico"),
    INTERMEDIO("Intermedio"),
    AVANZADO("Avanzado");

    private final String etiqueta;

    NivelDificultad(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /** @return nombre legible del nivel */
    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
