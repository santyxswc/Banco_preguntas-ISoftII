/**
 * @file AsignacionServiceTest.java
 * @brief Pruebas de la asignación de revisores.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.domain;

import co.unicauca.iso2.bancopreguntas.access.AsignacionImplRepository;
import co.unicauca.iso2.bancopreguntas.infra.events.EventListener;
import co.unicauca.iso2.bancopreguntas.infra.events.PreguntaEventPublisher;
import co.unicauca.iso2.bancopreguntas.infra.events.RevisorAsignadoEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @brief Pruebas de AsignacionService.
 */
class AsignacionServiceTest {

    private FakeQuestionRepository questionRepository;
    private QuestionService questionService;
    private AsignacionImplRepository asignacionRepository;
    private PreguntaEventPublisher eventPublisher;
    private AsignacionService asignacionService;

    @BeforeEach
    void setUp() {

        questionRepository = new FakeQuestionRepository();
        questionService = new QuestionService(questionRepository);

        asignacionRepository = new AsignacionImplRepository();
        eventPublisher = new PreguntaEventPublisher();

        asignacionService = new AsignacionService(
                questionService, asignacionRepository, eventPublisher);

        questionRepository.save(crearPreguntaPendiente("P-001"));
        questionRepository.save(new Question(
                "P-002", "Pregunta borrador", "Enunciado",
                new java.util.ArrayList<>(), "A", EstadoPregunta.BORRADOR));
    }

    private Question crearPreguntaPendiente(String id) {
        return new Question(
                id, "Pregunta pendiente", "Enunciado",
                new java.util.ArrayList<>(), "A",
                EstadoPregunta.PENDIENTE_REVISION);
    }

    @Test
    void asignarRevisoresCambiaEstadoDeLaPreguntaAEnRevision() {

        boolean exito = asignacionService.asignarRevisores(
                "P-001", List.of("REV-1", "REV-2"));

        assertTrue(exito);
        assertEquals(EstadoPregunta.EN_REVISION,
                questionService.findQuestionById("P-001").getEstado());
    }

    @Test
    void asignarRevisoresFallaSiLaPreguntaNoEstaPendienteDeRevision() {

        boolean exito = asignacionService.asignarRevisores(
                "P-002", List.of("REV-1"));

        assertFalse(exito);
        assertEquals(EstadoPregunta.BORRADOR,
                questionService.findQuestionById("P-002").getEstado());
    }

    @Test
    void asignarRevisoresFallaSiLaPreguntaNoExiste() {

        boolean exito = asignacionService.asignarRevisores(
                "NO-EXISTE", List.of("REV-1"));

        assertFalse(exito);
    }

    @Test
    void asignarRevisoresFallaSiNoHayRevisoresSeleccionados() {

        boolean exito = asignacionService.asignarRevisores(
                "P-001", List.of());

        assertFalse(exito);
    }

    @Test
    void asignarRevisoresPersisteElHistorialDeLaAsignacion() {

        asignacionService.asignarRevisores("P-001", List.of("REV-1", "REV-2"));

        List<Asignacion> historial = asignacionService.historialDe("P-001");

        assertEquals(1, historial.size());
        assertEquals(List.of("REV-1", "REV-2"), historial.get(0).getRevisorIds());
    }

    @Test
    void asignarRevisoresPublicaUnEventoRevisorAsignado() {

        final RevisorAsignadoEvent[] recibido = new RevisorAsignadoEvent[1];
        eventPublisher.subscribe(evento -> recibido[0] = evento);

        asignacionService.asignarRevisores("P-001", List.of("REV-1"));

        assertEquals("P-001", recibido[0].getPreguntaId());
        assertEquals(List.of("REV-1"), recibido[0].getRevisorIds());
    }

    @Test
    void unListenerQueFallaNoImpideQueLaAsignacionSeComplete() {

        eventPublisher.subscribe(evento -> {
            throw new RuntimeException("Fallo simulado de un listener");
        });

        boolean exito = asignacionService.asignarRevisores(
                "P-001", List.of("REV-1"));

        assertTrue(exito);
        assertEquals(EstadoPregunta.EN_REVISION,
                questionService.findQuestionById("P-001").getEstado());
    }

    @Test
    void unListenerQueFallaNoImpideQueOtrosListenersSeEjecuten() {

        final boolean[] segundoListenerLlamado = { false };

        eventPublisher.subscribe(evento -> {
            throw new RuntimeException("Fallo simulado");
        });
        eventPublisher.subscribe(evento -> segundoListenerLlamado[0] = true);

        asignacionService.asignarRevisores("P-001", List.of("REV-1"));

        assertTrue(segundoListenerLlamado[0]);
    }

    @Test
    void asignarRevisoresSinPublisherNoLanzaExcepcion() {

        AsignacionService servicioSinPublisher = new AsignacionService(
                questionService, asignacionRepository, null);

        boolean exito = servicioSinPublisher.asignarRevisores(
                "P-001", List.of("REV-1"));

        assertTrue(exito);
    }
}
