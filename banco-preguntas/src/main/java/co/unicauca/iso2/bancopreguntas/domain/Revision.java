/**
 * @file Revision.java
 * @brief Evaluación de un revisor sobre una pregunta (HU05).
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.domain;

import java.time.LocalDateTime;

/**
 * @brief Registro de la evaluación que un revisor deja sobre una pregunta.
 *
 * El revisor puede aprobar o rechazar la pregunta y dejar observaciones
 * para que el autor las lea y corrija si es necesario.
 */
public class Revision {

    private String id;
    /** Id de la pregunta evaluada. */
    private String preguntaId;
    /** Id del revisor que hizo la evaluación. */
    private String revisorId;
    /** Resultado de la revisión: APROBADA o RECHAZADA. */
    private EstadoPregunta veredicto;
    /** Comentarios y observaciones del revisor para el autor. */
    private String observaciones;
    private LocalDateTime fecha;

    public Revision() {
        this.fecha = LocalDateTime.now();
    }

    /**
     * @param id             identificador de la revisión
     * @param preguntaId     pregunta evaluada
     * @param revisorId      revisor que evalúa
     * @param veredicto      APROBADA o RECHAZADA
     * @param observaciones  observaciones del revisor
     * @param fecha          momento de la evaluación
     */
    public Revision(String id, String preguntaId, String revisorId,
                     EstadoPregunta veredicto, String observaciones,
                     LocalDateTime fecha) {
        this.id = id;
        this.preguntaId = preguntaId;
        this.revisorId = revisorId;
        this.veredicto = veredicto;
        this.observaciones = observaciones;
        this.fecha = fecha != null ? fecha : LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPreguntaId() { return preguntaId; }
    public void setPreguntaId(String preguntaId) { this.preguntaId = preguntaId; }

    public String getRevisorId() { return revisorId; }
    public void setRevisorId(String revisorId) { this.revisorId = revisorId; }

    public EstadoPregunta getVeredicto() { return veredicto; }
    public void setVeredicto(EstadoPregunta veredicto) { this.veredicto = veredicto; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    @Override
    public String toString() {
        return "Revisión " + id + " [" + veredicto + "] por " + revisorId;
    }
}
