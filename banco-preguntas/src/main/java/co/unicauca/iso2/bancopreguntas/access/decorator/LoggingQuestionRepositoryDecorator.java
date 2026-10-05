/**
 * @file LoggingQuestionRepositoryDecorator.java
 * @brief Patrón GoF Estructural: Decorator para añadir logging y auditoría al repositorio.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.access.decorator;

import co.unicauca.iso2.bancopreguntas.domain.EstadoPregunta;
import co.unicauca.iso2.bancopreguntas.domain.PaginaResultado;
import co.unicauca.iso2.bancopreguntas.domain.Question;
import co.unicauca.iso2.bancopreguntas.domain.QuestionFilter;
import co.unicauca.iso2.bancopreguntas.domain.QuestionRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * @brief Decorador que añade logging y métricas de auditoría a las operaciones del QuestionRepository.
 *
 * Sigue el principio Open/Closed al extender las capacidades del repositorio sin modificar su código base.
 */
public class LoggingQuestionRepositoryDecorator implements QuestionRepository {

    private static final Logger LOGGER = Logger.getLogger(LoggingQuestionRepositoryDecorator.class.getName());
    private final QuestionRepository wrapped;
    private final List<String> operationLogs = new ArrayList<>();

    public LoggingQuestionRepositoryDecorator(QuestionRepository wrapped) {
        this.wrapped = Objects.requireNonNull(wrapped, "El repositorio a decorar no puede ser nulo");
    }

    public List<String> getOperationLogs() {
        return Collections.unmodifiableList(operationLogs);
    }

    private void record(String operation) {
        operationLogs.add(operation);
        LOGGER.info("[AUDIT-DECORATOR] " + operation);
    }

    @Override
    public List<Question> list() {
        record("list()");
        return wrapped.list();
    }

    @Override
    public Question findById(String id) {
        record("findById(id=" + id + ")");
        return wrapped.findById(id);
    }

    @Override
    public List<Question> findByAutorId(String autorId) {
        record("findByAutorId(autorId=" + autorId + ")");
        return wrapped.findByAutorId(autorId);
    }

    @Override
    public List<Question> findByEstado(EstadoPregunta estado) {
        record("findByEstado(estado=" + estado + ")");
        return wrapped.findByEstado(estado);
    }

    @Override
    public boolean save(Question question) {
        record("save(question=" + (question != null ? question.getId() : "null") + ")");
        return wrapped.save(question);
    }

    @Override
    public boolean update(Question question) {
        record("update(id=" + (question != null ? question.getId() : "null") + ")");
        return wrapped.update(question);
    }

    @Override
    public boolean updateEstado(String id, EstadoPregunta nuevoEstado) {
        record("updateEstado(id=" + id + ", nuevoEstado=" + nuevoEstado + ")");
        return wrapped.updateEstado(id, nuevoEstado);
    }

    @Override
    public PaginaResultado<Question> buscar(QuestionFilter filtro) {
        record("buscar(filtro)");
        return wrapped.buscar(filtro);
    }
}
