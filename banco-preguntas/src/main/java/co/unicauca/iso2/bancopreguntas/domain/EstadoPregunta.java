/**
 * @file EstadoPregunta.java
 * @brief Estados del ciclo de vida de una pregunta.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.domain;

import java.awt.Color;
import java.util.EnumSet;
import java.util.Set;

/**
 * @brief Estados por los que pasa una pregunta del banco.
 *
 * Cada estado sabe con qué color se muestra, si permite edición y a qué
 * estados puede pasar, así las reglas del ciclo de vida no quedan
 * repartidas en if/switch por toda la aplicación.
 */
public enum EstadoPregunta {

    /** Pregunta en edición por su autor. */
    BORRADOR("Borrador"),
    /** Esperando que el administrador asigne revisores. */
    PENDIENTE_REVISION("Pendiente de revisión"),
    /** Con revisores asignados. */
    EN_REVISION("En revisión"),
    /** Retirada del banco; nunca se borra físicamente. */
    ARCHIVADA("Archivada");

    private final String etiqueta;

    EstadoPregunta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /** @return nombre legible del estado */
    public String getEtiqueta() {
        return etiqueta;
    }

    /**
     * @brief Color con el que se pinta el estado en la interfaz.
     * @return color asociado al estado
     */
    public Color colorUI() {
        return switch (this) {
            case BORRADOR           -> new Color(150, 150, 150);
            case PENDIENTE_REVISION -> new Color(230, 160,  20);
            case EN_REVISION        -> new Color( 30, 120, 210);
            case ARCHIVADA          -> new Color(180,  40,  40);
        };
    }

    /** @return true si la pregunta se puede editar en este estado */
    public boolean permiteEdicion() {
        return this == BORRADOR;
    }

    /** @return true si desde este estado se puede enviar a revisión */
    public boolean permiteEnviarARevision() {
        return this == BORRADOR;
    }

    /**
     * @brief Estados a los que se puede pasar desde este.
     * @return conjunto de estados destino válidos
     */
    public Set<EstadoPregunta> siguientesPermitidos() {
        return switch (this) {
            case BORRADOR           -> EnumSet.of(PENDIENTE_REVISION, ARCHIVADA);
            case PENDIENTE_REVISION -> EnumSet.of(EN_REVISION, ARCHIVADA);
            case EN_REVISION        -> EnumSet.of(ARCHIVADA);
            case ARCHIVADA          -> EnumSet.noneOf(EstadoPregunta.class);
        };
    }

    /**
     * @brief Indica si la transición hacia otro estado es válida.
     * @param destino estado al que se quiere pasar
     * @return true si la transición está permitida
     */
    public boolean puedeCambiarA(EstadoPregunta destino) {
        return destino != null && siguientesPermitidos().contains(destino);
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
