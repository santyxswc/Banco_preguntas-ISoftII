/**
 * @file RevisionServiceTest.java
 * @brief Pruebas unitarias para RevisionService (HU05).
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.domain;

import co.unicauca.iso2.bancopreguntas.access.AsignacionImplRepository;
import co.unicauca.iso2.bancopreguntas.access.RevisionImplRepository;
import co.unicauca.iso2.bancopreguntas.access.UsuarioImplRepository;
import co.unicauca.iso2.bancopreguntas.domain.validacion.ValidadorEstructuralPregunta;
import co.unicauca.iso2.bancopreguntas.infra.events.PreguntaEventPublisher;
import co.unicauca.iso2.bancopreguntas.infra.events.RevisionCompletadaEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RevisionServiceTest {

    private FakeQuestionRepository questionRepository;
    private UsuarioRepository usuarioRepository;
    private AsignacionRepository asignacionRepository;
    private RevisionRepository revisionRepository;
    private PreguntaEventPublisher eventPublisher;
    private QuestionService questionService;
    private RevisionService revisionService;
    private List<RevisionCompletadaEvent> eventosPublicados;

    @BeforeEach
    void setUp() {
        questionRepository = new FakeQuestionRepository();
        usuarioRepository = new UsuarioImplRepository();
        asignacionRepository = new AsignacionImplRepository();
        revisionRepository = new RevisionImplRepository();
        eventPublisher = new PreguntaEventPublisher();
        eventosPublicados = new ArrayList<>();

        eventPublisher.subscribeRevision(eventosPublicados::add);

        questionService = new QuestionService(
                questionRepository, usuarioRepository, new ValidadorEstructuralPregunta());

        revisionService = new RevisionService(
                questionService, revisionRepository, asignacionRepository, eventPublisher);
    }

    private Question crearPreguntaEnRevision(String id, String revisorId) {
        Question q = PreguntasDePrueba.preguntaValida("autor1");
        q.setId(id);
        q.setEstado(EstadoPregunta.EN_REVISION);
        questionRepository.save(q);

        Asignacion asig = new Asignacion("ASIG-" + id, id, List.of(revisorId), LocalDateTime.now());
        asignacionRepository.save(asig);
        return q;
    }

    @Test
    void listarPreguntasAsignadasDevuelveSoloPreguntasEnRevision() {
        crearPreguntaEnRevision("P1", "rev1");
        crearPreguntaEnRevision("P2", "rev1");

        // Pregunta asignada pero ya archivada
        Question p3 = PreguntasDePrueba.preguntaValida("autor1");
        p3.setId("P3");
        p3.setEstado(EstadoPregunta.ARCHIVADA);
        questionRepository.save(p3);
        asignacionRepository.save(new Asignacion("ASIG-P3", "P3", List.of("rev1"), LocalDateTime.now()));

        List<Question> asignadas = revisionService.listarPreguntasAsignadas("rev1");
        assertEquals(2, asignadas.size());
        assertTrue(asignadas.stream().anyMatch(q -> q.getId().equals("P1")));
        assertTrue(asignadas.stream().anyMatch(q -> q.getId().equals("P2")));
    }

    @Test
    void listarPreguntasAsignadasConRevisorVacioDevuelveVacio() {
        List<Question> asignadas = revisionService.listarPreguntasAsignadas(null);
        assertTrue(asignadas.isEmpty());
        asignadas = revisionService.listarPreguntasAsignadas("   ");
        assertTrue(asignadas.isEmpty());
    }

    @Test
    void evaluarConAprobacionCambiaEstadoYPublicaEvento() {
        crearPreguntaEnRevision("P10", "rev1");

        List<String> errores = revisionService.evaluar(
                "P10", "rev1", EstadoPregunta.APROBADA, "Cumple con todos los estándares.");

        assertTrue(errores.isEmpty(), "No deben existir errores al evaluar");

        Question actualizada = questionRepository.findById("P10");
        assertEquals(EstadoPregunta.APROBADA, actualizada.getEstado());

        List<Revision> historial = revisionRepository.findByPreguntaId("P10");
        assertEquals(1, historial.size());
        assertEquals(EstadoPregunta.APROBADA, historial.get(0).getVeredicto());
        assertEquals("Cumple con todos los estándares.", historial.get(0).getObservaciones());

        assertEquals(1, eventosPublicados.size());
        assertEquals("P10", eventosPublicados.get(0).getPreguntaId());
        assertEquals(EstadoPregunta.APROBADA, eventosPublicados.get(0).getVeredicto());
    }

    @Test
    void evaluarConRechazoCambiaEstadoARechazada() {
        crearPreguntaEnRevision("P20", "rev1");

        List<String> errores = revisionService.evaluar(
                "P20", "rev1", EstadoPregunta.RECHAZADA, "El distractor D no tiene justificación sólida.");

        assertTrue(errores.isEmpty());
        Question actualizada = questionRepository.findById("P20");
        assertEquals(EstadoPregunta.RECHAZADA, actualizada.getEstado());

        // La pregunta rechazada ahora permite edición al autor
        assertTrue(actualizada.getEstado().permiteEdicion());
    }

    @Test
    void evaluarValidaCamposObligatorios() {
        List<String> e1 = revisionService.evaluar(null, "rev1", EstadoPregunta.APROBADA, "Ok");
        assertFalse(e1.isEmpty());

        List<String> e2 = revisionService.evaluar("P1", null, EstadoPregunta.APROBADA, "Ok");
        assertFalse(e2.isEmpty());

        List<String> e3 = revisionService.evaluar("P1", "rev1", EstadoPregunta.BORRADOR, "Ok");
        assertFalse(e3.isEmpty());
    }

    @Test
    void noSePuedeEvaluarPreguntaInexistenteONoEnRevision() {
        List<String> e1 = revisionService.evaluar("NO_EXISTE", "rev1", EstadoPregunta.APROBADA, "Ok");
        assertTrue(e1.contains("La pregunta no existe."));

        Question qBorrador = PreguntasDePrueba.preguntaValida("autor1");
        qBorrador.setId("P_BORR");
        qBorrador.setEstado(EstadoPregunta.BORRADOR);
        questionRepository.save(qBorrador);

        List<String> e2 = revisionService.evaluar("P_BORR", "rev1", EstadoPregunta.APROBADA, "Ok");
        assertTrue(e2.contains("Solo se pueden evaluar preguntas en revisión."));
    }

    @Test
    void revisorNoAsignadoNoPuedeEvaluar() {
        crearPreguntaEnRevision("P30", "rev1");

        List<String> errores = revisionService.evaluar(
                "P30", "rev_intruso", EstadoPregunta.APROBADA, "Intento no autorizado");

        assertTrue(errores.contains("Solo un revisor asignado puede evaluar la pregunta."));
        assertEquals(EstadoPregunta.EN_REVISION, questionRepository.findById("P30").getEstado());
    }

    @Test
    void historialDeRetornaLasRevisionesRegistradas() {
        crearPreguntaEnRevision("P40", "rev1");
        revisionService.evaluar("P40", "rev1", EstadoPregunta.APROBADA, "Observación 1");

        List<Revision> hist = revisionService.historialDe("P40");
        assertEquals(1, hist.size());
        assertEquals("Observación 1", hist.get(0).getObservaciones());
    }
}
