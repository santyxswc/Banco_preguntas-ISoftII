package co.unicauca.iso2.bancopreguntas.domain;

/**
 * Catálogo de niveles de dificultad válidos para una pregunta (RF01.2).
 */
public enum NivelDificultad {

    BASICO("Básico"),
    INTERMEDIO("Intermedio"),
    AVANZADO("Avanzado");

    private final String etiqueta;

    NivelDificultad(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
