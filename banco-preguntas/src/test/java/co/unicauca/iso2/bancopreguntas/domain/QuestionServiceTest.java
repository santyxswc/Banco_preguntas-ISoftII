/**
 * @file QuestionServiceTest.java
 * @brief Pruebas del servicio de preguntas.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.domain;

import co.unicauca.iso2.bancopreguntas.access.UsuarioImplRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @brief Pruebas de QuestionService con un repositorio falso.
 */
class QuestionServiceTest {

    private FakeQuestionRepository repository;
    private QuestionService service;

    @BeforeEach
    void setUp() {

        repository = new FakeQuestionRepository();
        service = new QuestionService(repository);

        repository.save(new Question(
                "P-001", "Pregunta 1", "Enunciado 1",
                new ArrayList<>(), "A", EstadoPregunta.BORRADOR));

        repository.save(new Question(
                "P-002", "Pregunta 2", "Enunciado 2",
                new ArrayList<>(), "B",
                EstadoPregunta.PENDIENTE_REVISION));
    }

    @Test
    void listQuestionsDevuelveTodasLasPreguntas() {
        assertEquals(2, service.listQuestions().size());
    }

    @Test
    void findQuestionByIdDevuelveNullConIdVacio() {
        assertNull(service.findQuestionById(" "));
        assertNull(service.findQuestionById(null));
    }

    @Test
    void updateEstadoCambiaElEstadoCuandoLaTransicionEsValida() {

        assertTrue(service.updateEstado("P-001", EstadoPregunta.ARCHIVADA));
        assertEquals(EstadoPregunta.ARCHIVADA,
                service.findQuestionById("P-001").getEstado());
    }

    @Test
    void updateEstadoRechazaUnaTransicionNoPermitida() {

        assertFalse(service.updateEstado("P-001", EstadoPregunta.EN_REVISION));
        assertEquals(EstadoPregunta.BORRADOR,
                service.findQuestionById("P-001").getEstado());
    }

    @Test
    void updateEstadoDevuelveFalsoParaUnIdInexistente() {
        assertFalse(service.updateEstado("NO-EXISTE", EstadoPregunta.ARCHIVADA));
    }

    @Test
    void updateEstadoNotificaALosObservadoresRegistrados() {

        final boolean[] fueNotificado = { false };
        service.attach(subject -> fueNotificado[0] = true);

        service.updateEstado("P-001", EstadoPregunta.ARCHIVADA);

        assertTrue(fueNotificado[0]);
    }

    @Test
    void updateEstadoNoNotificaCuandoLaActualizacionFalla() {

        final boolean[] fueNotificado = { false };
        service.attach(subject -> fueNotificado[0] = true);

        service.updateEstado("NO-EXISTE", EstadoPregunta.ARCHIVADA);

        assertFalse(fueNotificado[0]);
    }

    @Test
    void contarPorEstadoCuentaCorrectamenteCadaEstado() {

        Map<EstadoPregunta, Long> conteo = service.contarPorEstado();

        assertEquals(1L, conteo.get(EstadoPregunta.BORRADOR));
        assertEquals(1L, conteo.get(EstadoPregunta.PENDIENTE_REVISION));
        assertEquals(0L, conteo.get(EstadoPregunta.ARCHIVADA));
    }

    @Test
    void porcentajePorEstadoSuma100() {

        double suma = service.porcentajePorEstado().values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        assertEquals(100.0, suma, 0.001);
    }

    @Test
    void porcentajePorEstadoEsCeroSinPreguntas() {

        QuestionService vacio = new QuestionService(new FakeQuestionRepository());

        assertTrue(vacio.porcentajePorEstado().values().stream()
                .allMatch(p -> p == 0.0));
    }

    @Test
    void validarEstructuraDetectaCamposFaltantes() {

        List<String> errores = service.validarEstructura(new Question());

        assertTrue(errores.stream().anyMatch(e -> e.startsWith("enunciado|")));
        assertTrue(errores.stream().anyMatch(e -> e.startsWith("contexto|")));
        assertTrue(errores.stream().anyMatch(e -> e.startsWith("opciones|")));
    }

    @Test
    void validarEstructuraAceptaPreguntaCompletaValida() {

        Question q = PreguntasDePrueba.valida("P-VAL", "U-001").construir();

        assertTrue(service.validarEstructura(q).isEmpty());
    }

    @Test
    void saveQuestionGuardaEnBorradorYNotifica() {

        final boolean[] fueNotificado = { false };
        service.attach(subject -> fueNotificado[0] = true);

        Question q = PreguntasDePrueba.valida("P-NUEVA", "U-001").construir();
        q.setEstado(null);

        assertTrue(service.saveQuestion(q));
        assertEquals(EstadoPregunta.BORRADOR,
                service.findQuestionById("P-NUEVA").getEstado());
        assertTrue(fueNotificado[0]);
    }

    @Test
    void saveQuestionRechazaUnaPreguntaIncompleta() {

        Question q = QuestionBuilder.nueva().conId("P-INCOMPLETA").construir();

        assertFalse(service.saveQuestion(q));
        assertNull(service.findQuestionById("P-INCOMPLETA"));
    }

    @Test
    void saveQuestionRechazaNulaOSinId() {

        assertFalse(service.saveQuestion(null));

        Question sinId = PreguntasDePrueba.valida("P-X", "U-001").construir();
        sinId.setId(" ");
        assertFalse(service.saveQuestion(sinId));
    }

    @Test
    void updateQuestionModificaUnaPreguntaEnBorrador() {

        service.saveQuestion(PreguntasDePrueba.valida("P-EDIT", "U-001").construir());

        Question cambios = PreguntasDePrueba.valida("P-EDIT", "U-001")
                .conTema("Tema modificado")
                .construir();

        assertTrue(service.updateQuestion(cambios));
        assertEquals("Tema modificado",
                service.findQuestionById("P-EDIT").getTema());
    }

    @Test
    void updateQuestionSoloPermiteEditarEnBorrador() {

        Question q = PreguntasDePrueba.valida("P-EDIT2", "U-001")
                .conEstado(EstadoPregunta.EN_REVISION)
                .construir();
        service.saveQuestion(q);

        q.setTema("Nuevo Tema Modificado");

        assertFalse(service.updateQuestion(q));
    }

    @Test
    void updateQuestionRechazaCambiosInvalidos() {

        service.saveQuestion(PreguntasDePrueba.valida("P-EDIT3", "U-001").construir());

        Question cambios = PreguntasDePrueba.valida("P-EDIT3", "U-001")
                .conContexto("corto")
                .construir();

        assertFalse(service.updateQuestion(cambios));
    }

    @Test
    void enviarARevisionCambiaEstadoAPendienteRevision() {

        service.saveQuestion(PreguntasDePrueba.valida("P-REV", "U-001").construir());

        List<String> errores = service.enviarARevision("P-REV", "U-001");

        assertTrue(errores.isEmpty(), "No debería tener errores: " + errores);
        assertEquals(EstadoPregunta.PENDIENTE_REVISION,
                service.findQuestionById("P-REV").getEstado());
    }

    @Test
    void enviarARevisionFallaSiNoEsElAutor() {

        service.saveQuestion(PreguntasDePrueba.valida("P-REV1", "U-001").construir());

        List<String> errores = service.enviarARevision("P-REV1", "U-OTRO");

        assertTrue(errores.get(0).contains("Solo el autor"));
    }

    @Test
    void enviarARevisionFallaSiLaPreguntaNoEstaEnBorrador() {

        service.saveQuestion(PreguntasDePrueba.valida("P-REV2", "U-001")
                .conEstado(EstadoPregunta.EN_REVISION).construir());

        List<String> errores = service.enviarARevision("P-REV2", "U-001");

        assertFalse(errores.isEmpty());
        assertEquals(EstadoPregunta.EN_REVISION,
                service.findQuestionById("P-REV2").getEstado());
    }

    @Test
    void enviarARevisionFallaSiLaPreguntaEstaIncompleta() {

        Question incompleta = new Question("P-INC", "Incompleta", "¿?",
                new ArrayList<>(), "A", EstadoPregunta.BORRADOR);
        incompleta.setAutorId("U-001");
        repository.save(incompleta);

        List<String> errores = service.enviarARevision("P-INC", "U-001");

        assertTrue(errores.get(0).contains("validación estructural"));
    }

    @Test
    void enviarARevisionFallaSiLaPreguntaNoExiste() {

        List<String> errores = service.enviarARevision("NO-EXISTE", "U-001");

        assertEquals("La pregunta no existe.", errores.get(0));
    }

    @Test
    void listByAutorRetornaSoloPreguntasDelAutor() {

        service.saveQuestion(PreguntasDePrueba.valida("P-A1", "AUTOR-X").construir());
        service.saveQuestion(PreguntasDePrueba.valida("P-A2", "AUTOR-Y").construir());

        List<Question> preguntasX = service.listByAutor("AUTOR-X");

        assertEquals(1, preguntasX.size());
        assertEquals("P-A1", preguntasX.get(0).getId());
        assertTrue(service.listByAutor(null).isEmpty());
    }

    @Test
    void listByEstadoFiltraPorEstado() {

        assertEquals(1, service.listByEstado(EstadoPregunta.PENDIENTE_REVISION).size());
        assertTrue(service.listByEstado(null).isEmpty());
    }

    @Test
    void buscarPaginadoFiltraYPagina() {

        for (int i = 1; i <= 5; i++) {
            service.saveQuestion(PreguntasDePrueba.valida("P-PAG-" + i, "AUTOR-P").construir());
        }

        PaginaResultado<Question> pagina = service.buscarPaginado(new QuestionFilter()
                .conAutorId("AUTOR-P")
                .conEstado(EstadoPregunta.BORRADOR)
                .conTamPagina(2)
                .conPagina(2));

        assertEquals(5, pagina.getTotalElementos());
        assertEquals(3, pagina.getTotalPaginas());
        assertEquals(1, pagina.getElementos().size());
    }

    @Test
    void buscarPaginadoConTextoLibreBuscaEnEnunciadoYContexto() {

        service.saveQuestion(PreguntasDePrueba.valida("P-TXT", "U-001")
                .conContexto("Un contexto sobre fotosíntesis en las plantas verdes.")
                .construir());

        PaginaResultado<Question> pagina = service.buscarPaginado(
                new QuestionFilter().conTextoLibre("FOTOSÍNTESIS"));

        assertEquals(1, pagina.getTotalElementos());
        assertEquals("P-TXT", pagina.getElementos().get(0).getId());
    }

    @Test
    void listarRevisoresDisponiblesDevuelveLosDocentes() {

        QuestionService conUsuarios = new QuestionService(
                repository, new UsuarioImplRepository());

        assertTrue(conUsuarios.listarRevisoresDisponibles().stream()
                .allMatch(u -> u.getRol() == Rol.AUTOR));
        assertFalse(conUsuarios.listarRevisoresDisponibles().isEmpty());
        assertTrue(service.listarRevisoresDisponibles().isEmpty());
    }

    @Test
    void listarRevisoresDeUnaPreguntaExcluyeASuAutor() {

        QuestionService conUsuarios = new QuestionService(
                repository, new UsuarioImplRepository());
        Question pregunta = new Question();
        pregunta.setAutorId("U-002");

        List<Usuario> revisores = conUsuarios.listarRevisoresDisponibles(pregunta);

        assertFalse(revisores.isEmpty());
        assertTrue(revisores.stream().noneMatch(u -> "U-002".equals(u.getId())));
        assertEquals(conUsuarios.listarRevisoresDisponibles().size() - 1,
                revisores.size());
    }
}
