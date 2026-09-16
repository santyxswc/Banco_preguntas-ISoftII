package co.unicauca.iso2.bancopreguntas.domain;

/**
 * Estados posibles de una pregunta del banco de preguntas Saber Pro.
 */
public enum EstadoPregunta {

    /** Gris — pregunta en edición por el autor. */
    BORRADOR("Borrador"),
    /** Ámbar — esperando que un admin asigne revisores. */
    PENDIENTE_REVISION("Pendiente de revisión"),
    /** Azul — revisores asignados, en proceso de revisión. */
    EN_REVISION("En revisión"),
    /** Rojo oscuro — descartada. */
    ELIMINADA("Eliminada");

    private final String etiqueta;

    EstadoPregunta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    /**
     * Color distintivo para representar el estado en la interfaz
     * (RF02.3).
     *
     * @return color AWT asociado al estado
     */
    public java.awt.Color getColor() {
        return switch (this) {
            case BORRADOR           -> new java.awt.Color(150, 150, 150); // gris
            case PENDIENTE_REVISION -> new java.awt.Color(230, 160,  20); // ámbar
            case EN_REVISION        -> new java.awt.Color( 30, 120, 210); // azul
            case ELIMINADA          -> new java.awt.Color(180,  40,  40); // rojo
        };
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
