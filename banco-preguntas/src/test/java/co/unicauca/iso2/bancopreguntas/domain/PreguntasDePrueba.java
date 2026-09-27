/**
 * @file PreguntasDePrueba.java
 * @brief Datos de prueba compartidos por las pruebas del dominio.
 * @author Santiago Caicedo
 */
package co.unicauca.iso2.bancopreguntas.domain;

/**
 * @brief Fábrica de preguntas válidas para las pruebas.
 */
public final class PreguntasDePrueba {

    private PreguntasDePrueba() {
    }

    /**
     * @param id      id de la pregunta
     * @param autorId autor de la pregunta
     * @return builder con todos los campos válidos, listo para ajustar
     */
    public static QuestionBuilder valida(String id, String autorId) {
        return QuestionBuilder.nueva()
                .conId(id)
                .conAutorId(autorId)
                .conNombre("Pregunta de prueba " + id)
                .conContexto("Contexto de la situación problema con más de veinte caracteres.")
                .conEnunciado("¿Cuál es la respuesta correcta a esta pregunta?")
                .conOpcion("A", "Opción A distractor")
                .conOpcion("B", "Opción B correcta")
                .conOpcion("C", "Opción C distractor")
                .conOpcion("D", "Opción D distractor")
                .conRespuestaCorrecta("B")
                .conJustificacion("Justificación válida con más de veinte caracteres.")
                .conBibliografia("Referencia bibliográfica 2026.")
                .conCompetencia("Lectura crítica")
                .conTema("Comprensión textual")
                .conSubtema("Inferencia")
                .conNivelDificultad(NivelDificultad.INTERMEDIO);
    }
}
