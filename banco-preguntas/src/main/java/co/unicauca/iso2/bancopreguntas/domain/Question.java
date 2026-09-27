/**
 * @file Question.java
 * @brief Entidad principal: pregunta del banco Saber Pro.
 * @author Santiago Caicedo
 */
package co.unicauca.iso2.bancopreguntas.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @brief Pregunta de selección múltiple con única respuesta.
 *
 * Contiene contexto, pregunta directa, opciones, respuesta correcta,
 * justificación, bibliografía, competencia, tema, subtema, nivel de
 * dificultad, estado y autor.
 */
public class Question {

    private String id;
    private String nombre;
    /** Situación problema que da contexto a la pregunta. */
    private String contexto;
    /** Pregunta directa. */
    private String enunciado;
    private List<QuestionDistractors> opciones = new ArrayList<>();
    /** Letra de la opción correcta, por ejemplo "B". */
    private String respuestaCorrecta;
    private String justificacion;
    private String bibliografia;
    private String competencia;
    private String tema;
    private String subtema;
    private NivelDificultad nivelDificultad;
    private EstadoPregunta estado;
    /** Id del usuario que creó la pregunta. */
    private String autorId;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    public Question() {
        this.fechaCreacion    = LocalDateTime.now();
        this.fechaModificacion = LocalDateTime.now();
    }

    /**
     * @brief Constructor con los datos básicos de la pregunta.
     * @param id                identificador
     * @param nombre            nombre corto
     * @param enunciado         pregunta directa
     * @param opciones          opciones de respuesta
     * @param respuestaCorrecta letra de la opción correcta
     * @param estado            estado inicial
     */
    public Question(String id, String nombre, String enunciado,
                    List<QuestionDistractors> opciones,
                    String respuestaCorrecta,
                    EstadoPregunta estado) {

        this.id               = id;
        this.nombre           = nombre;
        this.enunciado        = enunciado;
        this.opciones         = opciones;
        this.respuestaCorrecta = respuestaCorrecta;
        this.estado           = estado;
        this.fechaCreacion    = LocalDateTime.now();
        this.fechaModificacion = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getContexto() { return contexto; }
    public void setContexto(String contexto) {
        this.contexto = contexto;
        this.fechaModificacion = LocalDateTime.now();
    }

    public String getEnunciado() { return enunciado; }
    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
        this.fechaModificacion = LocalDateTime.now();
    }

    public List<QuestionDistractors> getOpciones() { return opciones; }
    public void setOpciones(List<QuestionDistractors> opciones) {
        this.opciones = opciones;
        this.fechaModificacion = LocalDateTime.now();
    }

    public String getRespuestaCorrecta() { return respuestaCorrecta; }
    public void setRespuestaCorrecta(String respuestaCorrecta) {
        this.respuestaCorrecta = respuestaCorrecta;
        this.fechaModificacion = LocalDateTime.now();
    }

    public String getJustificacion() { return justificacion; }
    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
        this.fechaModificacion = LocalDateTime.now();
    }

    public String getBibliografia() { return bibliografia; }
    public void setBibliografia(String bibliografia) {
        this.bibliografia = bibliografia;
        this.fechaModificacion = LocalDateTime.now();
    }

    public String getCompetencia() { return competencia; }
    public void setCompetencia(String competencia) {
        this.competencia = competencia;
        this.fechaModificacion = LocalDateTime.now();
    }

    public String getTema() { return tema; }
    public void setTema(String tema) {
        this.tema = tema;
        this.fechaModificacion = LocalDateTime.now();
    }

    public String getSubtema() { return subtema; }
    public void setSubtema(String subtema) {
        this.subtema = subtema;
        this.fechaModificacion = LocalDateTime.now();
    }

    public NivelDificultad getNivelDificultad() { return nivelDificultad; }
    public void setNivelDificultad(NivelDificultad nivelDificultad) {
        this.nivelDificultad = nivelDificultad;
        this.fechaModificacion = LocalDateTime.now();
    }

    public EstadoPregunta getEstado() { return estado; }
    public void setEstado(EstadoPregunta estado) {
        this.estado = estado;
        this.fechaModificacion = LocalDateTime.now();
    }

    public String getAutorId() { return autorId; }
    public void setAutorId(String autorId) { this.autorId = autorId; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    /**
     * @brief Texto de la opción marcada como correcta.
     * @return texto de la respuesta correcta o "" si no se encuentra
     */
    public String getTextoRespuestaCorrecta() {

        if (respuestaCorrecta == null || opciones == null) {
            return "";
        }

        for (QuestionDistractors opcion : opciones) {
            if (respuestaCorrecta.equalsIgnoreCase(opcion.getId())) {
                return opcion.getTexto();
            }
        }

        return "";
    }

    @Override
    public String toString() {
        return id + " - " + nombre;
    }
}
