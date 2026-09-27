/**
 * @file QuestionBuilderTest.java
 * @brief Pruebas del patrón Builder.
 * @author Santiago Caicedo
 */
package co.unicauca.iso2.bancopreguntas.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @brief Pruebas de QuestionBuilder.
 */
class QuestionBuilderTest {

    @Test
    void construirAsignaIdAutogeneradoCuandoNoSeIndica() {

        Question q = QuestionBuilder.nueva()
                .conEnunciado("¿Cuál es la capital de Colombia?")
                .construir();

        assertNotNull(q.getId());
        assertTrue(q.getId().startsWith("P-"));
    }

    @Test
    void construirRespetaElIdIndicadoExplicitamente() {

        Question q = QuestionBuilder.nueva()
                .conId("P-FIJO")
                .construir();

        assertEquals("P-FIJO", q.getId());
    }

    @Test
    void construirAsignaEstadoBorradorPorDefecto() {

        Question q = QuestionBuilder.nueva().construir();

        assertEquals(EstadoPregunta.BORRADOR, q.getEstado());
    }

    @Test
    void construirRespetaElEstadoIndicadoExplicitamente() {

        Question q = QuestionBuilder.nueva()
                .conEstado(EstadoPregunta.PENDIENTE_REVISION)
                .construir();

        assertEquals(EstadoPregunta.PENDIENTE_REVISION, q.getEstado());
    }

    @Test
    void construirAcumulaTodosLosCamposFluidamente() {

        Question q = PreguntasDePrueba.valida("P-1", "U-001").construir();

        assertEquals("Pregunta de prueba P-1", q.getNombre());
        assertEquals(4, q.getOpciones().size());
        assertEquals("B", q.getRespuestaCorrecta());
        assertEquals("U-001", q.getAutorId());
        assertEquals("Lectura crítica", q.getCompetencia());
        assertEquals("Comprensión textual", q.getTema());
        assertEquals("Inferencia", q.getSubtema());
        assertEquals(NivelDificultad.INTERMEDIO, q.getNivelDificultad());
    }

    @Test
    void conOpcionesReemplazaLasOpcionesAnteriores() {

        Question q = QuestionBuilder.nueva()
                .conOpcion("A", "Vieja")
                .conOpciones(List.of(new QuestionDistractors("A", "Nueva")))
                .construir();

        assertEquals(1, q.getOpciones().size());
        assertEquals("Nueva", q.getOpciones().get(0).getTexto());
    }

    @Test
    void conOpcionesNulasDejaLaListaVacia() {

        Question q = QuestionBuilder.nueva()
                .conOpcion("A", "Vieja")
                .conOpciones(null)
                .construir();

        assertTrue(q.getOpciones().isEmpty());
    }
}
