package co.unicauca.iso2.bancopreguntas.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación en memoria de QuestionRepository para pruebas
 * unitarias de QuestionService, sin depender de
 * QuestionImplRepository.
 */
public class FakeQuestionRepository implements QuestionRepository {

    private final List<Question> preguntas = new ArrayList<>();

    @Override
    public List<Question> list() {
        return new ArrayList<>(preguntas);
    }

    @Override
    public Question findById(String id) {

        if (id == null) {
            return null;
        }

        return preguntas.stream()
                .filter(pregunta -> id.equals(pregunta.getId()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean save(Question question) {

        if (question == null
                || question.getId() == null
                || question.getId().isBlank()) {

            return false;
        }

        if (findById(question.getId()) != null) {
            return false;
        }

        preguntas.add(question);

        return true;
    }

    @Override
    public boolean updateEstado(String id, EstadoPregunta nuevoEstado) {

        Question pregunta = findById(id);

        if (pregunta == null || nuevoEstado == null) {
            return false;
        }

        pregunta.setEstado(nuevoEstado);

        return true;
    }
}
