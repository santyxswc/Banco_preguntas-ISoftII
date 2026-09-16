package co.unicauca.iso2.bancopreguntas.domain;

import co.unicauca.iso2.bancopreguntas.infra.Observer;
import co.unicauca.iso2.bancopreguntas.infra.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        List<Question> preguntas = service.listQuestions();

        assertEquals(2, preguntas.size());
    }

    @Test
    void updateEstadoCambiaElEstadoDeUnaPreguntaExistente() {

        boolean actualizada = service.updateEstado(
                "P-001", EstadoPregunta.ELIMINADA);

        assertTrue(actualizada);

        assertEquals(
                EstadoPregunta.ELIMINADA,
                service.findQuestionById("P-001").getEstado());
    }

    @Test
    void updateEstadoDevuelveFalsoParaUnIdInexistente() {

        boolean actualizada = service.updateEstado(
                "NO-EXISTE", EstadoPregunta.ELIMINADA);

        assertFalse(actualizada);
    }

    @Test
    void updateEstadoNotificaALosObservadoresRegistrados() {

        // Observador de prueba: solo registra si fue notificado.
        final boolean[] fueNotificado = { false };

        Observer observadorDePrueba = new Observer() {
            @Override
            public void actualizar(Subject subject) {
                fueNotificado[0] = true;
            }
        };

        service.attach(observadorDePrueba);

        service.updateEstado("P-001", EstadoPregunta.ELIMINADA);

        assertTrue(fueNotificado[0]);
    }

    @Test
    void updateEstadoNoNotificaCuandoLaActualizacionFalla() {

        final boolean[] fueNotificado = { false };

        service.attach(subject -> fueNotificado[0] = true);

        boolean actualizada = service.updateEstado(
                "NO-EXISTE", EstadoPregunta.ELIMINADA);

        assertFalse(actualizada);
        assertFalse(fueNotificado[0]);
    }

    @Test
    void contarPorEstadoCuentaCorrectamenteCadaEstado() {

        Map<EstadoPregunta, Long> conteo = service.contarPorEstado();

        assertEquals(1L, conteo.get(EstadoPregunta.BORRADOR));
        assertEquals(
                1L, conteo.get(EstadoPregunta.PENDIENTE_REVISION));
        assertEquals(0L, conteo.get(EstadoPregunta.ELIMINADA));
    }

    @Test
    void porcentajePorEstadoSumaAproximadamente100() {

        Map<EstadoPregunta, Double> porcentajes = service.porcentajePorEstado();

        double suma = porcentajes.values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        assertEquals(100.0, suma, 0.001);
    }

    @Test
    void validarEstructuraDetectaCamposFaltantes() {
        Question q = new Question();
        List<String> errores = service.validarEstructura(q);
        assertFalse(errores.isEmpty());
        assertTrue(errores.stream().anyMatch(e -> e.contains("enunciado")));
        assertTrue(errores.stream().anyMatch(e -> e.contains("contexto")));
        assertTrue(errores.stream().anyMatch(e -> e.contains("opciones")));
    }

    @Test
    void validarEstructuraAceptaPreguntaCompletaValida() {
        Question q = crearPreguntaValida("P-VAL", "U-001");
        List<String> errores = service.validarEstructura(q);
        assertTrue(errores.isEmpty(), "No debería tener errores: " + errores);
    }

    @Test
    void enviarARevisionFallaSiNoEsElAutor() {
        Question q = crearPreguntaValida("P-REV1", "U-001");
        service.saveQuestion(q);

        List<String> errores = service.enviarARevision("P-REV1", "U-OTRO");
        assertFalse(errores.isEmpty());
        assertTrue(errores.get(0).contains("Solo el autor"));
    }

    @Test
    void enviarARevisionCambiaEstadoAPendienteRevision() {
        Question q = crearPreguntaValida("P-REV2", "U-001");
        service.saveQuestion(q);

        List<String> errores = service.enviarARevision("P-REV2", "U-001");
        assertTrue(errores.isEmpty(), "No debería tener errores: " + errores);

        Question actualizada = service.findQuestionById("P-REV2");
        assertEquals(EstadoPregunta.PENDIENTE_REVISION, actualizada.getEstado());
    }

    @Test
    void asignarRevisoresCambiaEstadoAEnRevision() {
        Question q = crearPreguntaValida("P-ASIG", "U-001");
        q.setEstado(EstadoPregunta.PENDIENTE_REVISION);
        service.saveQuestion(q);

        boolean exito = service.asignarRevisores("P-ASIG", List.of("REV-1", "REV-2"));
        assertTrue(exito);

        Question actualizada = service.findQuestionById("P-ASIG");
        assertEquals(EstadoPregunta.EN_REVISION, actualizada.getEstado());
    }

    @Test
    void listByAutorRetornaSoloPreguntasDelAutor() {
        Question q1 = crearPreguntaValida("P-A1", "AUTOR-X");
        Question q2 = crearPreguntaValida("P-A2", "AUTOR-Y");
        service.saveQuestion(q1);
        service.saveQuestion(q2);

        List<Question> preguntasX = service.listByAutor("AUTOR-X");
        assertEquals(1, preguntasX.size());
        assertEquals("P-A1", preguntasX.get(0).getId());
    }

    @Test
    void updateQuestionSoloPermiteEditarEnBorrador() {
        Question q = crearPreguntaValida("P-EDIT", "U-001");
        q.setEstado(EstadoPregunta.EN_REVISION);
        service.saveQuestion(q);

        q.setTema("Nuevo Tema Modificado");
        boolean exito = service.updateQuestion(q);
        assertFalse(exito, "No debe permitir editar en estado EN_REVISION");
    }

    private Question crearPreguntaValida(String id, String autorId) {
        Question q = new Question();
        q.setId(id);
        q.setAutorId(autorId);
        q.setNombre("Pregunta de prueba " + id);
        q.setEnunciado("¿Cuál es la respuesta correcta a esta pregunta de prueba?");
        q.setContexto("Este es el contexto detallado de la situación problema con más de 20 caracteres.");
        List<QuestionDistractors> ops = new ArrayList<>();
        ops.add(new QuestionDistractors("A", "Opción A distractor"));
        ops.add(new QuestionDistractors("B", "Opción B correcta"));
        ops.add(new QuestionDistractors("C", "Opción C distractor"));
        ops.add(new QuestionDistractors("D", "Opción D distractor"));
        q.setOpciones(ops);
        q.setRespuestaCorrecta("B");
        q.setJustificacion("Esta es la justificación válida con más de 20 caracteres explicativos.");
        q.setBibliografia("Referencia bibliográfica 2026.");
        q.setCompetencia("Lectura crítica");
        q.setTema("Comprensión");
        q.setSubtema("Inferencia");
        q.setNivelDificultad(NivelDificultad.INTERMEDIO);
        q.setEstado(EstadoPregunta.BORRADOR);
        return q;
    }
}
