/**
 * @file AsignacionService.java
 * @brief Lógica de negocio para asignar revisores a preguntas.
 * @author Santiago Caicedo
 */
package co.unicauca.iso2.bancopreguntas.domain;

import co.unicauca.iso2.bancopreguntas.infra.events.PreguntaEventPublisher;
import co.unicauca.iso2.bancopreguntas.infra.events.RevisorAsignadoEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @brief Asigna revisores a preguntas pendientes de revisión.
 *
 * Pasa la pregunta a EN_REVISION, guarda el historial y publica un
 * RevisorAsignadoEvent para que el envío de correos quede desacoplado
 * de este servicio.
 */
public class AsignacionService {

    private final QuestionService questionService;
    private final AsignacionRepository asignacionRepository;
    private final PreguntaEventPublisher eventPublisher;

    /**
     * @param questionService      servicio de preguntas
     * @param asignacionRepository repositorio de asignaciones
     * @param eventPublisher       publicador de eventos (puede ser null)
     */
    public AsignacionService(QuestionService questionService,
                             AsignacionRepository asignacionRepository,
                             PreguntaEventPublisher eventPublisher) {
        this.questionService = questionService;
        this.asignacionRepository = asignacionRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * @brief Asigna uno o más revisores a una pregunta pendiente.
     *
     * Rechaza la asignación si el autor de la pregunta aparece entre
     * los revisores seleccionados: nadie puede revisar su propia
     * pregunta.
     *
     * @param preguntaId id de la pregunta
     * @param revisorIds ids de los revisores seleccionados
     * @return true si la asignación se realizó
     */
    public boolean asignarRevisores(String preguntaId, List<String> revisorIds) {

        if (preguntaId == null || preguntaId.isBlank()
                || revisorIds == null || revisorIds.isEmpty()) {
            return false;
        }

        Question pregunta = questionService.findQuestionById(preguntaId);

        if (pregunta == null
                || pregunta.getEstado() != EstadoPregunta.PENDIENTE_REVISION) {
            return false;
        }

        // Un autor no puede ser revisor de su propia pregunta.
        if (revisorIds.contains(pregunta.getAutorId())) {
            return false;
        }

        boolean actualizada = questionService.updateEstado(
                preguntaId, EstadoPregunta.EN_REVISION);

        if (!actualizada) {
            return false;
        }

        LocalDateTime ahora = LocalDateTime.now();

        Asignacion asignacion = new Asignacion(
                null, preguntaId, new ArrayList<>(revisorIds), ahora);
        asignacionRepository.save(asignacion);

        // El correo se envía después de confirmar la asignación,
        // así un fallo en el envío no la deshace.
        if (eventPublisher != null) {
            eventPublisher.publish(new RevisorAsignadoEvent(
                    preguntaId, new ArrayList<>(revisorIds), ahora));
        }

        return true;
    }

    /**
     * @param preguntaId id de la pregunta
     * @return historial de asignaciones de la pregunta
     */
    public List<Asignacion> historialDe(String preguntaId) {
        return asignacionRepository.findByPreguntaId(preguntaId);
    }
}