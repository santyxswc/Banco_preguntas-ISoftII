/**
 * @file RevisionService.java
 * @brief Lógica de negocio para la revisión de preguntas (HU05).
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.domain;

import co.unicauca.iso2.bancopreguntas.infra.events.EventListener;
import co.unicauca.iso2.bancopreguntas.infra.events.PreguntaEventPublisher;
import co.unicauca.iso2.bancopreguntas.infra.events.RevisionCompletadaEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @brief Permite a un revisor evaluar preguntas asignadas.
 *
 * El revisor puede aprobar o rechazar una pregunta dejando observaciones
 * para el autor. La evaluación cambia el estado de la pregunta y publica
 * un evento para notificar a los implicados.
 */
public class RevisionService {

    private final QuestionService questionService;
    private final RevisionRepository revisionRepository;
    private final AsignacionRepository asignacionRepository;
    private final PreguntaEventPublisher eventPublisher;

    /**
     * @param questionService      servicio de preguntas
     * @param revisionRepository   repositorio de revisiones
     * @param asignacionRepository repositorio de asignaciones
     * @param eventPublisher       publicador de eventos (puede ser null)
     */
    public RevisionService(QuestionService questionService,
                            RevisionRepository revisionRepository,
                            AsignacionRepository asignacionRepository,
                            PreguntaEventPublisher eventPublisher) {
        this.questionService = questionService;
        this.revisionRepository = revisionRepository;
        this.asignacionRepository = asignacionRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * @brief Lista las preguntas asignadas a un revisor.
     *
     * Busca las asignaciones del revisor y luego obtiene las preguntas
     * que están EN_REVISION.
     *
     * @param revisorId id del revisor
     * @return preguntas asignadas al revisor que están en revisión
     */
    public List<Question> listarPreguntasAsignadas(String revisorId) {

        if (revisorId == null || revisorId.isBlank()) {
            return new ArrayList<>();
        }

        List<Asignacion> asignaciones = asignacionRepository.list();
        List<Question> resultado = new ArrayList<>();

        for (Asignacion asignacion : asignaciones) {
            if (asignacion.getRevisorIds() != null
                    && asignacion.getRevisorIds().contains(revisorId)) {

                Question pregunta = questionService.findQuestionById(
                        asignacion.getPreguntaId());

                if (pregunta != null
                        && pregunta.getEstado() == EstadoPregunta.EN_REVISION) {
                    resultado.add(pregunta);
                }
            }
        }

        return resultado;
    }

    /**
     * @brief Registra la evaluación de un revisor sobre una pregunta.
     *
     * Solo un revisor asignado puede evaluar una pregunta en estado
     * EN_REVISION. El veredicto debe ser APROBADA o RECHAZADA.
     *
     * @param preguntaId    id de la pregunta
     * @param revisorId     id del revisor
     * @param veredicto     APROBADA o RECHAZADA
     * @param observaciones observaciones del revisor
     * @return errores encontrados (vacía si la evaluación se registró)
     */
    public List<String> evaluar(String preguntaId, String revisorId,
                                 EstadoPregunta veredicto, String observaciones) {

        List<String> errores = new ArrayList<>();

        if (preguntaId == null || preguntaId.isBlank()) {
            errores.add("El id de la pregunta es obligatorio.");
            return errores;
        }

        if (revisorId == null || revisorId.isBlank()) {
            errores.add("El id del revisor es obligatorio.");
            return errores;
        }

        if (veredicto != EstadoPregunta.APROBADA
                && veredicto != EstadoPregunta.RECHAZADA) {
            errores.add("El veredicto debe ser APROBADA o RECHAZADA.");
            return errores;
        }

        Question pregunta = questionService.findQuestionById(preguntaId);

        if (pregunta == null) {
            errores.add("La pregunta no existe.");
            return errores;
        }

        if (pregunta.getEstado() != EstadoPregunta.EN_REVISION) {
            errores.add("Solo se pueden evaluar preguntas en revisión.");
            return errores;
        }

        if (!esRevisorAsignado(preguntaId, revisorId)) {
            errores.add("Solo un revisor asignado puede evaluar la pregunta.");
            return errores;
        }

        // Guardar la revisión
        Revision revision = new Revision(
                null, preguntaId, revisorId, veredicto,
                observaciones, LocalDateTime.now());
        revisionRepository.save(revision);

        // Cambiar el estado de la pregunta
        boolean cambiado = questionService.updateEstado(preguntaId, veredicto);

        if (!cambiado) {
            errores.add("No fue posible actualizar el estado de la pregunta.");
            return errores;
        }

        // Publicar evento para notificar al autor
        if (eventPublisher != null) {
            eventPublisher.publishRevision(new RevisionCompletadaEvent(
                    preguntaId, revisorId, veredicto, observaciones,
                    LocalDateTime.now()));
        }

        return errores;
    }

    /**
     * @param preguntaId id de la pregunta
     * @return historial de revisiones de la pregunta
     */
    public List<Revision> historialDe(String preguntaId) {
        return revisionRepository.findByPreguntaId(preguntaId);
    }

    /**
     * @brief Verifica si un usuario es revisor asignado de una pregunta.
     * @param preguntaId id de la pregunta
     * @param revisorId  id del revisor
     * @return true si el usuario está asignado como revisor
     */
    private boolean esRevisorAsignado(String preguntaId, String revisorId) {

        List<Asignacion> asignaciones =
                asignacionRepository.findByPreguntaId(preguntaId);

        for (Asignacion asignacion : asignaciones) {
            if (asignacion.getRevisorIds() != null
                    && asignacion.getRevisorIds().contains(revisorId)) {
                return true;
            }
        }

        return false;
    }
}
