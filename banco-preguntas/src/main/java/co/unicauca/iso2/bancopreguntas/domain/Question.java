package co.unicauca.iso2.bancopreguntas.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad principal del dominio: una pregunta del banco de preguntas
 * Saber Pro.
 *
 * Además de los campos originales (id, nombre, enunciado, opciones,
 * respuestaCorrecta, estado) incorpora los campos requeridos por
 * RF01.1: contexto, justificacion, bibliografia, competencia, tema,
 * subtema, nivelDificultad y autorId.
 */
public class Question {

    private String id;
    private String nombre;

    /** Texto de contexto / situación problema (RF01.1). */
    private String contexto;

    /** Pregunta directa / enunciado principal (RF01.1). */
    private String enunciado;

    private List<QuestionDistractors> opciones = new ArrayList<>();
    private String respuestaCorrecta;

    /** Explicación de por qué la respuesta es correcta (RF01.1). */
    private String justificacion;

    /** Referencias bibliográficas (RF01.1). */
    private String bibliografia;

    /** Competencia evaluada (RF01.1). */
    private String competencia;

    /** Tema (RF01.1). */
    private String tema;

    /** Subtema (RF01.1). */
    private String subtema;

    /** Nivel de dificultad del catálogo válido (RF01.2). */
    private NivelDificultad nivelDificultad;

    private EstadoPregunta estado;

    /** Id del autor que creó la pregunta (RF01.4, RF02.1, RF03.1). */
    private String autorId;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    public Question() {
        this.fechaCreacion    = LocalDateTime.now();
        this.fechaModificacion = LocalDateTime.now();
    }

    /** Constructor compacto (compatibilidad con código existente). */
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

    // ----------------------------------------------------------------
    // Getters y setters
    // ----------------------------------------------------------------

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

    /**
     * Letra (id) de la opción que es correcta. Por ejemplo "B".
     */
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

    // ----------------------------------------------------------------
    // Métodos de negocio
    // ----------------------------------------------------------------

    /**
     * Busca, entre las opciones de la pregunta, el texto de la
     * opción marcada como correcta.
     *
     * @return texto de la respuesta correcta, o cadena vacía si no
     *         se encuentra
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
        // Usado por el JComboBox de GUIQuestions: "Id - Nombre".
        return id + " - " + nombre;
    }
}
