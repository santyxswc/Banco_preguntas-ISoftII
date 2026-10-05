/**
 * @file LoggingQuestionRepositoryDecoratorTest.java
 * @brief Pruebas unitarias para el patrón GoF Decorator (LoggingQuestionRepositoryDecorator).
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.access.decorator;

import co.unicauca.iso2.bancopreguntas.domain.EstadoPregunta;
import co.unicauca.iso2.bancopreguntas.domain.FakeQuestionRepository;
import co.unicauca.iso2.bancopreguntas.domain.PreguntasDePrueba;
import co.unicauca.iso2.bancopreguntas.domain.Question;
import co.unicauca.iso2.bancopreguntas.domain.QuestionFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoggingQuestionRepositoryDecoratorTest {

    private FakeQuestionRepository baseRepo;
    private LoggingQuestionRepositoryDecorator decorator;

    @BeforeEach
    void setUp() {
        baseRepo = new FakeQuestionRepository();
        decorator = new LoggingQuestionRepositoryDecorator(baseRepo);
    }

    @Test
    void constructorLanzaExcepcionSiRepositorioEsNulo() {
        assertThrows(NullPointerException.class, () -> new LoggingQuestionRepositoryDecorator(null));
    }

    @Test
    void decoradorRegistraLogsDeOperaciones() {
        Question q = PreguntasDePrueba.preguntaValida("autor1");
        q.setId("P-DEC-1");

        decorator.save(q);
        decorator.findById("P-DEC-1");
        decorator.updateEstado("P-DEC-1", EstadoPregunta.PENDIENTE_REVISION);
        decorator.findByAutorId("autor1");
        decorator.findByEstado(EstadoPregunta.PENDIENTE_REVISION);
        decorator.buscar(new QuestionFilter());
        decorator.list();
        decorator.update(q);

        List<String> logs = decorator.getOperationLogs();
        assertNotNull(logs);
        assertEquals(8, logs.size());
        assertTrue(logs.get(0).contains("save"));
        assertTrue(logs.get(1).contains("findById"));
        assertTrue(logs.get(2).contains("updateEstado"));
        assertTrue(logs.get(3).contains("findByAutorId"));
        assertTrue(logs.get(4).contains("findByEstado"));
        assertTrue(logs.get(5).contains("buscar"));
        assertTrue(logs.get(6).contains("list"));
        assertTrue(logs.get(7).contains("update"));
    }
}
