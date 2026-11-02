package co.unicauca.iso2.taller09.questionservice.repository;

import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import co.unicauca.iso2.taller09.questionservice.domain.Question;
import co.unicauca.iso2.taller09.questionservice.domain.QuestionBuilder;
import co.unicauca.iso2.taller09.questionservice.domain.QuestionFilter;
import co.unicauca.iso2.taller09.questionservice.repository.jpa.JpaQuestionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import(JpaQuestionRepository.class)
class JpaQuestionRepositoryTest {

    @Autowired
    private JpaQuestionRepository repository;

    private Question pregunta(Long autor, String competencia, String tema, String enunciado) {
        return QuestionBuilder.aQuestion()
                .context("Contexto de prueba para la persistencia de la pregunta.")
                .statement(enunciado)
                .option("A", "Builder").option("B", "Observer").option("C", "Facade").option("D", "Adapter")
                .correctOption("A")
                .justification("Builder separa la construcción de la representación.")
                .bibliography("Gamma et al., 1994")
                .competency(competencia)
                .topic(tema)
                .subtopic("Creacionales")
                .difficulty("medio")
                .author(autor)
                .status(QuestionStatus.DRAFT)
                .build();
    }

    @Test
    @DisplayName("Guarda la pregunta con sus cuatro opciones en orden y la recupera igual")
    void guardaYRecupera() {
        Question guardada = repository.save(pregunta(2L, "Diseño", "Patrones", "¿Qué patrón construye objetos paso a paso?"));

        assertNotNull(guardada.getId());
        Question leida = repository.findById(guardada.getId()).orElseThrow();
        assertEquals("¿Qué patrón construye objetos paso a paso?", leida.getText());
        assertEquals(List.of("A", "B", "C", "D"), leida.getOptions().stream().map(o -> o.getId()).toList());
        assertEquals("Builder", leida.getOptions().get(0).getText());
        assertEquals("MEDIO", leida.getDifficulty());
        assertEquals(QuestionStatus.DRAFT, leida.getStatus());
        assertEquals(1, repository.count());
    }

    @Test
    @DisplayName("Actualizar una pregunta existente no crea otra")
    void actualiza() {
        Question guardada = repository.save(pregunta(2L, "Diseño", "Patrones", "¿Enunciado original de la pregunta?"));
        Question editada = repository.findById(guardada.getId()).orElseThrow();
        editada.updateContentFrom(pregunta(2L, "Arquitectura", "Microservicios", "¿Enunciado editado de la pregunta?"));
        repository.save(editada);

        assertEquals(1, repository.count());
        Question leida = repository.findById(guardada.getId()).orElseThrow();
        assertEquals("Arquitectura", leida.getCompetency());
        assertEquals("¿Enunciado editado de la pregunta?", leida.getText());
        assertEquals(4, leida.getOptions().size());
    }

    @Test
    @DisplayName("Filtra por autor, competencia sin distinguir mayúsculas y palabra clave, de la más reciente a la más antigua")
    void filtra() {
        Question primera = repository.save(pregunta(2L, "Diseño", "Patrones", "¿Qué patrón notifica cambios de estado?"));
        Question segunda = repository.save(pregunta(2L, "Diseño", "Arquitectura limpia", "¿Qué capa contiene el dominio?"));
        repository.save(pregunta(3L, "Bases de datos", "Normalización", "¿Qué forma normal elimina dependencias parciales?"));

        List<Question> delAutor = repository.findMatching(new QuestionFilter(2L, null, null, null, null, null));
        assertEquals(List.of(segunda.getId(), primera.getId()), delAutor.stream().map(Question::getId).toList());

        assertEquals(2, repository.findMatching(new QuestionFilter(null, null, "diseño", null, null, null)).size());
        assertEquals(1, repository.findMatching(new QuestionFilter(null, null, null, null, null, "NOTIFICA")).size());
        assertEquals(1, repository.findMatching(new QuestionFilter(null, null, null, "normalización", "MEDIO", null)).size());
        assertTrue(repository.findMatching(new QuestionFilter(9L, null, null, null, null, null)).isEmpty());
        assertEquals(2, repository.findByAuthorId(2L).size());
    }
}
