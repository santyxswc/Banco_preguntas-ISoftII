/**
 * @file BancoPreguntasFacadeTest.java
 * @brief Pruebas unitarias para el patrón GoF Facade (BancoPreguntasFacade).
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.domain;

import co.unicauca.iso2.bancopreguntas.access.AsignacionImplRepository;
import co.unicauca.iso2.bancopreguntas.access.RevisionImplRepository;
import co.unicauca.iso2.bancopreguntas.access.UsuarioImplRepository;
import co.unicauca.iso2.bancopreguntas.domain.facade.BancoPreguntasFacade;
import co.unicauca.iso2.bancopreguntas.domain.validacion.ValidadorEstructuralPregunta;
import co.unicauca.iso2.bancopreguntas.infra.events.PreguntaEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BancoPreguntasFacadeTest {

    private BancoPreguntasFacade facade;
    private FakeQuestionRepository questionRepository;
    private UsuarioRepository usuarioRepository;
    private AsignacionRepository asignacionRepository;
    private RevisionRepository revisionRepository;

    @BeforeEach
    void setUp() {
        questionRepository = new FakeQuestionRepository();
        usuarioRepository = new UsuarioImplRepository();
        asignacionRepository = new AsignacionImplRepository();
        revisionRepository = new RevisionImplRepository();

        QuestionService questionService = new QuestionService(
                questionRepository, usuarioRepository, new ValidadorEstructuralPregunta());
        AsignacionService asignacionService = new AsignacionService(
                questionService, asignacionRepository, new PreguntaEventPublisher());
        RevisionService revisionService = new RevisionService(
                questionService, revisionRepository, asignacionRepository, new PreguntaEventPublisher());

        facade = new BancoPreguntasFacade(
                questionService, asignacionService, revisionService, usuarioRepository);
    }

    @Test
    void flujoCompletoMedianteFachada() {
        // 1. Autor crea pregunta válida (HU01)
        Question q = PreguntasDePrueba.preguntaValida("autor1");
        List<String> erroresCreacion = facade.crearPregunta(q);
        assertTrue(erroresCreacion.isEmpty(), "La creación debe ser exitosa");

        Question guardada = facade.obtenerPreguntaPorId(q.getId());
        assertNotNull(guardada);
        assertEquals(EstadoPregunta.BORRADOR, guardada.getEstado());

        // 2. Autor envía a revisión (HU02)
        boolean cambio = facade.cambiarEstado(q.getId(), EstadoPregunta.PENDIENTE_REVISION);
        assertTrue(cambio);
        assertEquals(EstadoPregunta.PENDIENTE_REVISION, facade.obtenerPreguntaPorId(q.getId()).getEstado());

        // 3. Admin lista pendientes y asigna 2 revisores (HU04)
        List<Question> pendientes = facade.listarPreguntasPendientesRevision();
        assertEquals(1, pendientes.size());

        List<String> erroresAsignacion = facade.asignarRevisores(q.getId(), List.of("rev1", "rev2"));
        assertTrue(erroresAsignacion.isEmpty());
        assertEquals(EstadoPregunta.EN_REVISION, facade.obtenerPreguntaPorId(q.getId()).getEstado());

        // 4. Revisor lista sus preguntas asignadas y evalúa (HU05)
        List<Question> asignadasRevisor1 = facade.listarPreguntasAsignadas("rev1");
        assertEquals(1, asignadasRevisor1.size());

        List<String> erroresEvaluacion = facade.evaluarPregunta(
                q.getId(), "rev1", EstadoPregunta.APROBADA, "Excelente pregunta.");
        assertTrue(erroresEvaluacion.isEmpty());

        // 5. Pregunta pasa a APROBADA y tiene historial
        assertEquals(EstadoPregunta.APROBADA, facade.obtenerPreguntaPorId(q.getId()).getEstado());
        List<Revision> hist = facade.obtenerHistorialRevisiones(q.getId());
        assertEquals(1, hist.size());
        assertEquals(EstadoPregunta.APROBADA, hist.get(0).getVeredicto());
    }

    @Test
    void validarEstructuralmentePreguntaInvalida() {
        Question qInvalida = new Question(); // vacía
        List<String> errores = facade.validarEstructuralmente(qInvalida);
        assertFalse(errores.isEmpty());
    }

    @Test
    void listarUsuariosYPorAutor() {
        Question q = PreguntasDePrueba.preguntaValida("autor1");
        facade.crearPregunta(q);

        List<Question> delAutor = facade.listarPorAutor("autor1");
        assertEquals(1, delAutor.size());

        List<Usuario> usuarios = facade.listarUsuarios();
        assertFalse(usuarios.isEmpty());
    }
}
