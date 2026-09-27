/**
 * @file PreguntaController.java
 * @brief Controlador MVC para crear y editar preguntas.
 * @author Santiago Caicedo
 */
package co.unicauca.iso2.bancopreguntas.presentation;

import co.unicauca.iso2.bancopreguntas.domain.EstadoPregunta;
import co.unicauca.iso2.bancopreguntas.domain.NivelDificultad;
import co.unicauca.iso2.bancopreguntas.domain.Question;
import co.unicauca.iso2.bancopreguntas.domain.QuestionBuilder;
import co.unicauca.iso2.bancopreguntas.domain.QuestionService;

import java.util.List;

/**
 * @brief Recibe los datos de los formularios, arma la pregunta con
 *        QuestionBuilder, la valida y la guarda con QuestionService.
 *
 * Las vistas (GUICrearPregunta y GUIEditarPregunta) solo recogen los
 * datos y muestran los errores que devuelve este controlador.
 */
public class PreguntaController {

    private final QuestionService questionService;

    /** @param questionService servicio de preguntas */
    public PreguntaController(QuestionService questionService) {
        this.questionService = questionService;
    }

    /** @brief Datos tal como vienen del formulario, sin validar. */
    public static class DatosFormularioPregunta {
        public String contexto;
        public String enunciado;
        public String opcionA;
        public String opcionB;
        public String opcionC;
        public String opcionD;
        public String respuestaCorrecta;
        public String justificacion;
        public String bibliografia;
        public String competencia;
        public String tema;
        public String subtema;
        public NivelDificultad nivelDificultad;
        public String autorId;
    }

    /**
     * @brief Crea una pregunta nueva en BORRADOR.
     * @param datos datos del formulario
     * @return errores "campo|mensaje" (vacía si se guardó)
     */
    public List<String> crearPregunta(DatosFormularioPregunta datos) {

        Question pregunta = armarPregunta(QuestionBuilder.nueva(), datos)
                .conAutorId(datos.autorId)
                .conEstado(EstadoPregunta.BORRADOR)
                .construir();

        List<String> errores = questionService.validarEstructura(pregunta);
        if (!errores.isEmpty()) {
            return errores;
        }

        if (!questionService.saveQuestion(pregunta)) {
            return List.of("id|No se pudo guardar la pregunta (el ID ya existe).");
        }

        return List.of();
    }

    /**
     * @brief Guarda los cambios de una pregunta en BORRADOR.
     * @param original pregunta que se está editando
     * @param datos    datos nuevos del formulario
     * @return errores "campo|mensaje" (vacía si se guardó)
     */
    public List<String> editarPregunta(Question original,
                                       DatosFormularioPregunta datos) {

        Question pregunta = armarPregunta(QuestionBuilder.nueva(), datos)
                .conId(original.getId())
                .conAutorId(original.getAutorId())
                .conEstado(original.getEstado())
                .construir();
        pregunta.setFechaCreacion(original.getFechaCreacion());

        List<String> errores = questionService.validarEstructura(pregunta);
        if (!errores.isEmpty()) {
            return errores;
        }

        if (!questionService.updateQuestion(pregunta)) {
            return List.of("id|Solo se pueden editar preguntas en borrador.");
        }

        return List.of();
    }

    private QuestionBuilder armarPregunta(QuestionBuilder builder,
                                          DatosFormularioPregunta datos) {
        return builder
                .conNombre(nombreCorto(datos.enunciado))
                .conContexto(datos.contexto)
                .conEnunciado(datos.enunciado)
                .conOpcion("A", datos.opcionA)
                .conOpcion("B", datos.opcionB)
                .conOpcion("C", datos.opcionC)
                .conOpcion("D", datos.opcionD)
                .conRespuestaCorrecta(datos.respuestaCorrecta)
                .conJustificacion(datos.justificacion)
                .conBibliografia(datos.bibliografia)
                .conCompetencia(datos.competencia)
                .conTema(datos.tema)
                .conSubtema(datos.subtema)
                .conNivelDificultad(datos.nivelDificultad);
    }

    private String nombreCorto(String enunciado) {
        if (enunciado == null) {
            return "";
        }
        String t = enunciado.trim();
        return t.length() > 50 ? t.substring(0, 50) + "…" : t;
    }
}
