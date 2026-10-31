/**
 * @file CleanJpaPersistenceH2Test.java
 * @brief Integration tests for clean JPA persistence and business rules on H2 (Taller 10).
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.jpa;

import co.unicauca.iso2.bancopreguntas.jpa.domain.model.CleanOption;
import co.unicauca.iso2.bancopreguntas.jpa.domain.model.CleanQuestion;
import co.unicauca.iso2.bancopreguntas.jpa.domain.model.CleanQuestionStatus;
import co.unicauca.iso2.bancopreguntas.jpa.domain.model.CleanQuiz;
import co.unicauca.iso2.bancopreguntas.jpa.domain.repository.CleanQuestionRepository;
import co.unicauca.iso2.bancopreguntas.jpa.domain.repository.CleanQuizRepository;
import co.unicauca.iso2.bancopreguntas.jpa.domain.service.CleanQuizService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @brief Verifies persistence, JOINED inheritance and domain invariant isolation on H2.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CleanJpaTestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CleanJpaPersistenceH2Test {

    @Autowired
    private CleanQuestionRepository questionRepository;

    @Autowired
    private CleanQuizRepository quizRepository;

    @Autowired
    private CleanQuizService quizService;

    private List<CleanOption> crearCuatroOpciones() {
        return List.of(
                new CleanOption("Opción A distractor", false),
                new CleanOption("Opción B respuesta correcta", true),
                new CleanOption("Opción C distractor", false),
                new CleanOption("Opción D distractor", false)
        );
    }

    @Test
    @DisplayName("Caso 1 & 2: Crear y consultar pregunta con persistencia limpia y cascada de opciones")
    void crearYConsultarPregunta() {
        CleanQuestion creada = quizService.createQuestion(
                "docente-autor-1",
                "¿Qué patrón aísla el dominio de JPA?",
                "Contexto de arquitectura de persistencia limpia",
                "El patrón Repository junto con un DomainMapper previene la contaminación del modelo.",
                "Diseño de Software",
                "Persistencia",
                crearCuatroOpciones()
        );

        assertNotNull(creada.getId());
        assertEquals(CleanQuestionStatus.DRAFT, creada.getStatus());
        assertEquals(4, creada.getOptions().size());

        CleanQuestion recuperada = quizService.getQuestion(creada.getId());
        assertNotNull(recuperada);
        assertEquals("docente-autor-1", recuperada.getAuthorId());
        assertEquals(4, recuperada.getOptions().size());
        assertTrue(recuperada.getOptions().stream().anyMatch(CleanOption::isCorrect));
    }

    @Test
    @DisplayName("Caso 3: Clasificar pregunta asociándola a competencia y tema")
    void clasificarPregunta() {
        CleanQuestion creada = quizService.createQuestion(
                "docente-autor-2",
                "¿Qué anotación define herencia en JPA?",
                "Contexto JPA",
                "La anotación @Inheritance permite elegir la estrategia JOINED.",
                "Sin clasificar",
                "Sin tema",
                crearCuatroOpciones()
        );

        CleanQuestion clasificada = quizService.classifyQuestion(
                creada.getId(),
                "Competencias Ciudadanas y Tecnológicas",
                "Mapeo Objeto-Relacional"
        );

        assertEquals("Competencias Ciudadanas y Tecnológicas", clasificada.getCompetence());
        assertEquals("Mapeo Objeto-Relacional", clasificada.getTopic());
    }

    @Test
    @DisplayName("Caso 4: Enviar pregunta a revisión validando precondiciones")
    void enviarPreguntaARevision() {
        CleanQuestion creada = quizService.createQuestion(
                "docente-autor-3",
                "¿Cuál es el beneficio de la estrategia JOINED?",
                "Contexto de normalización relacional",
                "Normaliza la base de datos dividiendo atributos comunes y específicos.",
                "Ingeniería de Software",
                "Bases de datos",
                crearCuatroOpciones()
        );

        CleanQuestion enviada = quizService.submitForReview(creada.getId());
        assertEquals(CleanQuestionStatus.PENDING_REVIEW, enviada.getStatus());
    }

    @Test
    @DisplayName("Caso 5: Crear cuestionario con preguntas aprobadas/publicadas")
    void crearCuestionarioConPreguntasAprobadas() {
        CleanQuestion q1 = quizService.createQuestion(
                "docente-1", "¿Pregunta 1?", "Contexto 1", "Justificación 1",
                "Comp 1", "Tema 1", crearCuatroOpciones());
        quizService.approveQuestion(q1.getId());

        CleanQuestion q2 = quizService.createQuestion(
                "docente-1", "¿Pregunta 2?", "Contexto 2", "Justificación 2",
                "Comp 1", "Tema 1", crearCuatroOpciones());
        quizService.approveQuestion(q2.getId());

        CleanQuiz quiz = quizService.createQuiz(
                "Simulacro Saber Pro 2026",
                "Módulo de Ingeniería de Software",
                List.of(q1.getId(), q2.getId())
        );

        assertNotNull(quiz.getId());
        assertEquals(2, quiz.getQuestions().size());

        CleanQuiz recuperado = quizRepository.findById(quiz.getId()).orElse(null);
        assertNotNull(recuperado);
        assertEquals("Simulacro Saber Pro 2026", recuperado.getTitle());
        assertEquals(2, recuperado.getQuestions().size());
    }

    @Test
    @DisplayName("Regla de ciclo de vida: Impide agregar preguntas no aprobadas a un cuestionario")
    void impideAgregarPreguntasNoAprobadasACuestionario() {
        CleanQuestion borrador = quizService.createQuestion(
                "docente-1", "¿Pregunta en borrador?", "Contexto", "Justificación",
                "Comp 1", "Tema 1", crearCuatroOpciones());

        // El borrador está en estado DRAFT, no APPROVED
        assertThrows(IllegalStateException.class, () ->
                quizService.createQuiz(
                        "Cuestionario Inválido",
                        "Módulo Inválido",
                        List.of(borrador.getId())
                ));
    }
}
