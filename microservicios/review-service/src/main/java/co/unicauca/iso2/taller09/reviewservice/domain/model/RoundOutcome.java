package co.unicauca.iso2.taller09.reviewservice.domain.model;

/**
 * Resultado de una ronda de revisión por pares (HU-06).
 * <p>
 * Una ronda está {@link #IN_PROGRESS} mientras algún revisor asignado no haya registrado su
 * decisión; cuando todos evalúan, la política de veredicto la cierra como {@link #APPROVED}
 * o {@link #REJECTED}.
 */
public enum RoundOutcome {
    IN_PROGRESS("En curso"),
    APPROVED("Aprobada"),
    REJECTED("Rechazada");

    private final String label;

    RoundOutcome(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public boolean isClosed() {
        return this != IN_PROGRESS;
    }
}
