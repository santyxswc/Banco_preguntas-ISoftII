/**
 * @file ValidadorEstructuralPreguntaTest.java
 * @brief Pruebas de la validación estructural (HU03).
 * @author Santiago Caicedo
 */
package co.unicauca.iso2.bancopreguntas.domain.validacion;

import co.unicauca.iso2.bancopreguntas.domain.PreguntasDePrueba;
import co.unicauca.iso2.bancopreguntas.domain.Question;
import co.unicauca.iso2.bancopreguntas.domain.QuestionBuilder;
import co.unicauca.iso2.bancopreguntas.domain.QuestionDistractors;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @brief Pruebas de ValidadorEstructuralPregunta.
 */
class ValidadorEstructuralPreguntaTest {

    private final ReglaValidacion validador = new ValidadorEstructuralPregunta();

    private List<String> validar(QuestionBuilder builder) {
        return validador.validar(builder.construir());
    }

    private boolean contiene(List<String> errores, String campo) {
        return errores.stream().anyMatch(e -> e.startsWith(campo + "|"));
    }

    @Test
    void preguntaCompletaNoTieneErrores() {
        List<String> errores = validar(PreguntasDePrueba.valida("P-1", "U-1"));
        assertTrue(errores.isEmpty(), "No debería tener errores: " + errores);
    }

    @Test
    void preguntaNulaEsInvalida() {
        assertEquals(1, validador.validar(null).size());
    }

    @Test
    void detectaTodosLosCamposObligatoriosFaltantes() {

        List<String> errores = validar(QuestionBuilder.nueva());

        for (String campo : List.of("enunciado", "contexto", "justificacion",
                "bibliografia", "competencia", "tema", "subtema",
                "nivelDificultad", "respuestaCorrecta", "opciones")) {
            assertTrue(contiene(errores, campo), "Falta error de " + campo);
        }
    }

    @Test
    void contextoDebeTenerLongitudMinima() {
        List<String> errores = validar(PreguntasDePrueba.valida("P-1", "U-1")
                .conContexto("Muy corto"));
        assertTrue(contiene(errores, "contexto"));
    }

    @Test
    void justificacionNoPuedeSuperarElMaximo() {
        List<String> errores = validar(PreguntasDePrueba.valida("P-1", "U-1")
                .conJustificacion("x".repeat(2001)));
        assertTrue(contiene(errores, "justificacion"));
    }

    @Test
    void enunciadoDebeTenerLongitudMinima() {
        List<String> errores = validar(PreguntasDePrueba.valida("P-1", "U-1")
                .conEnunciado("¿Qué?"));
        assertTrue(contiene(errores, "enunciado"));
    }

    @Test
    void enunciadoDebeTenerUnaUnicaPregunta() {
        List<String> errores = validar(PreguntasDePrueba.valida("P-1", "U-1")
                .conEnunciado("¿Cuál es la capital? ¿Y cuál es su población?"));
        assertTrue(errores.contains("enunciado|Debe haber una única pregunta directa."));
    }

    @Test
    void debenExistirExactamenteCuatroOpciones() {
        List<String> errores = validar(PreguntasDePrueba.valida("P-1", "U-1")
                .conOpciones(List.of(
                        new QuestionDistractors("A", "Uno"),
                        new QuestionDistractors("B", "Dos"),
                        new QuestionDistractors("C", "Tres"))));
        assertTrue(contiene(errores, "opciones"));
    }

    @Test
    void laRespuestaCorrectaDebeSerUnaDeLasOpciones() {
        List<String> errores = validar(PreguntasDePrueba.valida("P-1", "U-1")
                .conRespuestaCorrecta("E"));
        assertTrue(contiene(errores, "respuestaCorrecta"));
    }

    @Test
    void noPermiteOpcionesVacias() {
        List<String> errores = validar(PreguntasDePrueba.valida("P-1", "U-1")
                .conOpciones(List.of(
                        new QuestionDistractors("A", "Opción A distractor"),
                        new QuestionDistractors("B", "Opción B correcta"),
                        new QuestionDistractors("C", " "),
                        new QuestionDistractors("D", "Opción D distractor"))));
        assertTrue(contiene(errores, "opcion_C"));
    }

    @Test
    void noPermiteOpcionesRepetidas() {
        List<String> errores = validar(PreguntasDePrueba.valida("P-1", "U-1")
                .conOpciones(List.of(
                        new QuestionDistractors("A", "Misma opción"),
                        new QuestionDistractors("B", "misma  OPCIÓN"),
                        new QuestionDistractors("C", "Opción C distractor"),
                        new QuestionDistractors("D", "Opción D distractor"))));
        assertTrue(errores.contains("opciones|Las opciones deben ser distintas entre sí."));
    }

    @Test
    void noPermiteTodasLasAnteriores() {
        List<String> errores = validar(PreguntasDePrueba.valida("P-1", "U-1")
                .conOpciones(List.of(
                        new QuestionDistractors("A", "Opción A distractor"),
                        new QuestionDistractors("B", "Opción B correcta"),
                        new QuestionDistractors("C", "Opción C distractor"),
                        new QuestionDistractors("D", "Todas las anteriores"))));
        assertTrue(contiene(errores, "opcion_D"));
    }

    @Test
    void noPermiteNingunaDeLasAnterioresSinImportarTildesNiMayusculas() {
        List<String> errores = validar(PreguntasDePrueba.valida("P-1", "U-1")
                .conOpciones(List.of(
                        new QuestionDistractors("A", "NINGÚNA de las anteriores"),
                        new QuestionDistractors("B", "Opción B correcta"),
                        new QuestionDistractors("C", "Opción C distractor"),
                        new QuestionDistractors("D", "Opción D distractor"))));
        assertTrue(contiene(errores, "opcion_A"));
    }

    @Test
    void opcionesDebenTenerLongitudSimilar() {
        List<String> errores = validar(PreguntasDePrueba.valida("P-1", "U-1")
                .conOpciones(List.of(
                        new QuestionDistractors("A", "Sí"),
                        new QuestionDistractors("B", "La opción correcta es claramente esta que es muy larga"),
                        new QuestionDistractors("C", "No"),
                        new QuestionDistractors("D", "Tal vez"))));
        assertTrue(errores.stream().anyMatch(e -> e.contains("longitud similar")));
    }

    @Test
    void opcionNoPuedeSuperarElMaximo() {
        String larga = "a".repeat(301);
        List<String> errores = validar(PreguntasDePrueba.valida("P-1", "U-1")
                .conOpciones(List.of(
                        new QuestionDistractors("A", larga),
                        new QuestionDistractors("B", "b".repeat(300)),
                        new QuestionDistractors("C", "c".repeat(300)),
                        new QuestionDistractors("D", "d".repeat(300)))));
        assertTrue(contiene(errores, "opcion_A"));
        assertFalse(contiene(errores, "opcion_B"));
    }
}
