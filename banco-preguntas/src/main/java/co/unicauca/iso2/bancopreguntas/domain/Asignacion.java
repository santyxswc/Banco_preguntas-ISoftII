/**
 * @file Asignacion.java
 * @brief Registro de una asignación de revisores a una pregunta.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @brief Guarda qué revisores se asignaron a una pregunta y cuándo.
 *
 * Se mantiene separada de Question para que la entidad principal no
 * cargue con el historial de revisiones.
 */
public class Asignacion {

    private String id;
    private String preguntaId;
    private List<String> revisorIds;
    private LocalDateTime fecha;

    public Asignacion() {
        this.revisorIds = new ArrayList<>();
    }

    /**
     * @param id         identificador de la asignación (puede ser null)
     * @param preguntaId pregunta a la que se asignan los revisores
     * @param revisorIds ids de los revisores
     * @param fecha      momento de la asignación
     */
    public Asignacion(String id, String preguntaId,
                       List<String> revisorIds, LocalDateTime fecha) {
        this.id = id;
        this.preguntaId = preguntaId;
        this.revisorIds = revisorIds != null
                ? new ArrayList<>(revisorIds) : new ArrayList<>();
        this.fecha = fecha;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPreguntaId() { return preguntaId; }
    public void setPreguntaId(String preguntaId) { this.preguntaId = preguntaId; }

    public List<String> getRevisorIds() { return revisorIds; }
    public void setRevisorIds(List<String> revisorIds) {
        this.revisorIds = revisorIds != null
                ? new ArrayList<>(revisorIds) : new ArrayList<>();
    }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
