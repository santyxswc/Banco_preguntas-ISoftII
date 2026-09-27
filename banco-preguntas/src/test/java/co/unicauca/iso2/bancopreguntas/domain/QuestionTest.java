/**
 * @file QuestionTest.java
 * @brief Pruebas de la entidad Question.
 * @author Santiago Caicedo
 */
package co.unicauca.iso2.bancopreguntas.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @brief Pruebas de Question y QuestionDistractors.
 */
class QuestionTest {

    @Test
    void getTextoRespuestaCorrectaDevuelveElTextoDeLaOpcion() {

        Question q = new Question("P-1", "Nombre", "¿Enunciado?",
                List.of(new QuestionDistractors("A", "Uno"),
                        new QuestionDistractors("B", "Dos")),
                "b", EstadoPregunta.BORRADOR);

        assertEquals("Dos", q.getTextoRespuestaCorrecta());
    }

    @Test
    void getTextoRespuestaCorrectaDevuelveVacioSiNoHayCoincidencia() {

        Question q = new Question("P-1", "Nombre", "¿Enunciado?",
                new ArrayList<>(), "C", EstadoPregunta.BORRADOR);

        assertEquals("", q.getTextoRespuestaCorrecta());
        q.setRespuestaCorrecta(null);
        assertEquals("", q.getTextoRespuestaCorrecta());
    }

    @Test
    void constructorInicializaFechas() {

        Question q = new Question();

        assertNotNull(q.getFechaCreacion());
        assertNotNull(q.getFechaModificacion());
    }

    @Test
    void modificarUnCampoActualizaLaFechaDeModificacion() {

        Question q = new Question();
        LocalDateTime antigua = LocalDateTime.of(2020, 1, 1, 0, 0);
        q.setFechaModificacion(antigua);

        q.setEstado(EstadoPregunta.PENDIENTE_REVISION);

        assertTrue(q.getFechaModificacion().isAfter(antigua));
    }

    @Test
    void settersYGettersConservanLosValores() {

        Question q = new Question();
        q.setId("P-9");
        q.setNombre("Nombre");
        q.setContexto("Contexto");
        q.setEnunciado("Enunciado");
        q.setJustificacion("Justificación");
        q.setBibliografia("Bibliografía");
        q.setCompetencia("Competencia");
        q.setTema("Tema");
        q.setSubtema("Subtema");
        q.setNivelDificultad(NivelDificultad.AVANZADO);
        q.setAutorId("U-1");

        assertEquals("P-9", q.getId());
        assertEquals("Contexto", q.getContexto());
        assertEquals("Justificación", q.getJustificacion());
        assertEquals("Bibliografía", q.getBibliografia());
        assertEquals("Subtema", q.getSubtema());
        assertEquals(NivelDificultad.AVANZADO, q.getNivelDificultad());
        assertEquals("U-1", q.getAutorId());
        assertEquals("P-9 - Nombre", q.toString());
    }

    @Test
    void opcionSeMuestraConSuLetra() {

        QuestionDistractors op = new QuestionDistractors();
        op.setId("A");
        op.setTexto("Texto");

        assertEquals("A. Texto", op.toString());
    }
}
