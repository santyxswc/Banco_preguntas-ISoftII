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
        final boolean[] fueNotificado = {false};

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

        final boolean[] fueNotificado = {false};

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

        Map<EstadoPregunta, Double> porcentajes =
                service.porcentajePorEstado();

        double suma = porcentajes.values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        assertEquals(100.0, suma, 0.001);
    }
}
