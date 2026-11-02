package co.unicauca.iso2.taller09.common.enums;

/**
 * Estados del ciclo de vida de una pregunta, con su etiqueta en español y el color con el
 * que se visualizan (HU-02: "los estados se deben visualizar con colores").
 */
public enum QuestionStatus {
    DRAFT("Borrador", "#969696"),
    PENDING_REVIEW("Pendiente de revisión", "#E6A014"),
    IN_REVIEW("En revisión", "#1E78D2"),
    APPROVED("Aprobada", "#28A745"),
    REJECTED("Rechazada", "#DC3545"),
    PUBLISHED("Publicada", "#6F42C1");

    private final String label;
    private final String color;

    QuestionStatus(String label, String color) {
        this.label = label;
        this.color = color;
    }

    public String label() {
        return label;
    }

    public String color() {
        return color;
    }

    /** El autor solo puede editar el contenido mientras la pregunta está en borrador o fue rechazada. */
    public boolean isEditable() {
        return this == DRAFT || this == REJECTED;
    }

    public boolean canTransitionTo(QuestionStatus nextState) {
        if (nextState == null) return false;
        return switch (this) {
            case DRAFT -> nextState == PENDING_REVIEW;
            case PENDING_REVIEW -> nextState == IN_REVIEW;
            case IN_REVIEW -> nextState == APPROVED || nextState == REJECTED;
            case REJECTED -> nextState == PENDING_REVIEW || nextState == DRAFT;
            case APPROVED -> nextState == PUBLISHED;
            case PUBLISHED -> false;
        };
    }
}
