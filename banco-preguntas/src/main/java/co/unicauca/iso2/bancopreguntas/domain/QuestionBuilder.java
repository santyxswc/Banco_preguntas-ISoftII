/**
 * @file QuestionBuilder.java
 * @brief Construcción paso a paso de preguntas (patrón Builder).
 * @author Santiago Caicedo
 */
package co.unicauca.iso2.bancopreguntas.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @brief Builder fluido para crear instancias de Question.
 *
 * Si no se indica id se genera uno ("P-XXXXXXXX") y si no se indica
 * estado la pregunta queda en BORRADOR.
 */
public class QuestionBuilder {

    private String id;
    private String nombre;
    private String contexto;
    private String enunciado;
    private final List<QuestionDistractors> opciones = new ArrayList<>();
    private String respuestaCorrecta;
    private String justificacion;
    private String bibliografia;
    private String competencia;
    private String tema;
    private String subtema;
    private NivelDificultad nivelDificultad;
    private EstadoPregunta estado;
    private String autorId;

    /** @return un builder vacío */
    public static QuestionBuilder nueva() {
        return new QuestionBuilder();
    }

    public QuestionBuilder conId(String id) {
        this.id = id;
        return this;
    }

    public QuestionBuilder conNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public QuestionBuilder conContexto(String contexto) {
        this.contexto = contexto;
        return this;
    }

    public QuestionBuilder conEnunciado(String enunciado) {
        this.enunciado = enunciado;
        return this;
    }

    /**
     * @brief Agrega una opción de respuesta.
     * @param id    letra de la opción
     * @param texto texto de la opción
     * @return el mismo builder
     */
    public QuestionBuilder conOpcion(String id, String texto) {
        this.opciones.add(new QuestionDistractors(id, texto));
        return this;
    }

    /**
     * @brief Reemplaza todas las opciones.
     * @param opciones nuevas opciones
     * @return el mismo builder
     */
    public QuestionBuilder conOpciones(List<QuestionDistractors> opciones) {
        this.opciones.clear();
        if (opciones != null) {
            this.opciones.addAll(opciones);
        }
        return this;
    }

    public QuestionBuilder conRespuestaCorrecta(String letra) {
        this.respuestaCorrecta = letra;
        return this;
    }

    public QuestionBuilder conJustificacion(String justificacion) {
        this.justificacion = justificacion;
        return this;
    }

    public QuestionBuilder conBibliografia(String bibliografia) {
        this.bibliografia = bibliografia;
        return this;
    }

    public QuestionBuilder conCompetencia(String competencia) {
        this.competencia = competencia;
        return this;
    }

    public QuestionBuilder conTema(String tema) {
        this.tema = tema;
        return this;
    }

    public QuestionBuilder conSubtema(String subtema) {
        this.subtema = subtema;
        return this;
    }

    public QuestionBuilder conNivelDificultad(NivelDificultad nivel) {
        this.nivelDificultad = nivel;
        return this;
    }

    public QuestionBuilder conEstado(EstadoPregunta estado) {
        this.estado = estado;
        return this;
    }

    public QuestionBuilder conAutorId(String autorId) {
        this.autorId = autorId;
        return this;
    }

    /**
     * @brief Crea la pregunta con los datos acumulados.
     * @return pregunta construida
     */
    public Question construir() {

        Question pregunta = new Question();

        pregunta.setId(id != null && !id.isBlank()
                ? id
                : "P-" + UUID.randomUUID().toString()
                        .substring(0, 8).toUpperCase());

        pregunta.setNombre(nombre);
        pregunta.setContexto(contexto);
        pregunta.setEnunciado(enunciado);
        pregunta.setOpciones(new ArrayList<>(opciones));
        pregunta.setRespuestaCorrecta(respuestaCorrecta);
        pregunta.setJustificacion(justificacion);
        pregunta.setBibliografia(bibliografia);
        pregunta.setCompetencia(competencia);
        pregunta.setTema(tema);
        pregunta.setSubtema(subtema);
        pregunta.setNivelDificultad(nivelDificultad);
        pregunta.setAutorId(autorId);
        pregunta.setEstado(estado != null ? estado : EstadoPregunta.BORRADOR);

        return pregunta;
    }
}
