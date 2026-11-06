/**
 * @file Revision.java
 * @brief Entidad (Entity) del contexto de Revisión por Pares: la evaluación de una pregunta por un revisor.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 */
package co.edu.unicauca.saberpro.domain.model;

import co.edu.unicauca.saberpro.domain.exception.ReglaNegocioException;

import java.util.Objects;
import java.util.Optional;

/**
 * @brief Entidad identificada por su ID de evaluación.
 *
 * Reglas que protege:
 * - El autor de la pregunta no puede ser su revisor.
 * - El dictamen se emite una sola vez.
 * - Un dictamen RECHAZADA exige una observación pedagógica.
 */
public class Revision {

    private final Long idEvaluacion;
    private final Long preguntaId;
    private final Revisor revisor;
    private Dictamen dictamen;
    private String observacion;

    /**
     * @brief Asigna un revisor a una pregunta.
     * @param idEvaluacion identificador único de la evaluación.
     * @param pregunta pregunta que se va a evaluar.
     * @param revisor docente que evalúa.
     * @throws ReglaNegocioException si falta un dato o si el revisor es el autor de la pregunta.
     */
    public Revision(Long idEvaluacion, Pregunta pregunta, Revisor revisor) {
        if (idEvaluacion == null) {
            throw new ReglaNegocioException("La revisión debe tener un ID de evaluación.");
        }
        if (pregunta == null || revisor == null) {
            throw new ReglaNegocioException("La revisión necesita una pregunta y un revisor.");
        }
        if (revisor.getIdDocente().equals(pregunta.getAutorId())) {
            throw new ReglaNegocioException("El autor de una pregunta no puede ser su revisor.");
        }
        this.idEvaluacion = idEvaluacion;
        this.preguntaId = pregunta.getId();
        this.revisor = revisor;
    }

    /**
     * @brief Registra el dictamen del revisor.
     * @param dictamen veredicto (no nulo).
     * @param observacion observación pedagógica; obligatoria si el dictamen es RECHAZADA.
     * @throws ReglaNegocioException si ya había dictamen o si falta la observación de un rechazo.
     */
    public void emitirDictamen(Dictamen dictamen, String observacion) {
        if (this.dictamen != null) {
            throw new ReglaNegocioException("La revisión ya tiene un dictamen emitido.");
        }
        if (dictamen == null) {
            throw new ReglaNegocioException("Debe indicarse un dictamen.");
        }
        if (dictamen == Dictamen.RECHAZADA && (observacion == null || observacion.isBlank())) {
            throw new ReglaNegocioException("Un rechazo debe incluir una observación pedagógica.");
        }
        this.dictamen = dictamen;
        this.observacion = observacion == null ? null : observacion.trim();
    }

    public Long getIdEvaluacion() {
        return idEvaluacion;
    }

    public Long getPreguntaId() {
        return preguntaId;
    }

    public Revisor getRevisor() {
        return revisor;
    }

    public Optional<Dictamen> getDictamen() {
        return Optional.ofNullable(dictamen);
    }

    public Optional<String> getObservacion() {
        return Optional.ofNullable(observacion);
    }

    /** @brief Igualdad por identidad: solo cuenta el ID de evaluación. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Revision)) return false;
        return idEvaluacion.equals(((Revision) o).idEvaluacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEvaluacion);
    }
}
