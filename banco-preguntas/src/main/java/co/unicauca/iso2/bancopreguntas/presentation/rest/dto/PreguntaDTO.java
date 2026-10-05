/**
 * @file PreguntaDTO.java
 * @brief Objeto de transferencia de datos para la entidad Pregunta.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.presentation.rest.dto;

import co.unicauca.iso2.bancopreguntas.domain.EstadoPregunta;
import co.unicauca.iso2.bancopreguntas.domain.NivelDificultad;
import co.unicauca.iso2.bancopreguntas.domain.Question;
import co.unicauca.iso2.bancopreguntas.domain.QuestionDistractors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PreguntaDTO {

    private String id;
    private String nombre;
    private String contexto;
    private String enunciado;
    private String opcionA;
    private String opcionB;
    private String opcionC;
    private String opcionD;
    private String respuestaCorrecta;
    private String justificacion;
    private String bibliografia;
    private String competencia;
    private String tema;
    private String subtema;
    private NivelDificultad nivelDificultad;
    private EstadoPregunta estado;
    private String estadoHex;
    private String autorId;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    public PreguntaDTO() {}

    public static PreguntaDTO fromEntity(Question q) {
        if (q == null) return null;
        PreguntaDTO dto = new PreguntaDTO();
        dto.id = q.getId();
        dto.nombre = q.getNombre();
        dto.contexto = q.getContexto();
        dto.enunciado = q.getEnunciado();
        if (q.getOpciones() != null) {
            for (QuestionDistractors d : q.getOpciones()) {
                if (d != null && d.getId() != null) {
                    switch (d.getId().toUpperCase()) {
                        case "A" -> dto.opcionA = d.getTexto();
                        case "B" -> dto.opcionB = d.getTexto();
                        case "C" -> dto.opcionC = d.getTexto();
                        case "D" -> dto.opcionD = d.getTexto();
                    }
                }
            }
        }
        dto.respuestaCorrecta = q.getRespuestaCorrecta();
        dto.justificacion = q.getJustificacion();
        dto.bibliografia = q.getBibliografia();
        dto.competencia = q.getCompetencia();
        dto.tema = q.getTema();
        dto.subtema = q.getSubtema();
        dto.nivelDificultad = q.getNivelDificultad();
        dto.estado = q.getEstado();
        dto.estadoHex = q.getEstado() != null ? q.getEstado().colorHex() : null;
        dto.autorId = q.getAutorId();
        dto.fechaCreacion = q.getFechaCreacion();
        dto.fechaModificacion = q.getFechaModificacion();
        return dto;
    }

    public Question toEntity() {
        Question q = new Question();
        q.setId(this.id);
        q.setNombre(this.nombre);
        q.setContexto(this.contexto);
        q.setEnunciado(this.enunciado);

        List<QuestionDistractors> opciones = new ArrayList<>();
        if (this.opcionA != null) opciones.add(new QuestionDistractors("A", this.opcionA));
        if (this.opcionB != null) opciones.add(new QuestionDistractors("B", this.opcionB));
        if (this.opcionC != null) opciones.add(new QuestionDistractors("C", this.opcionC));
        if (this.opcionD != null) opciones.add(new QuestionDistractors("D", this.opcionD));
        q.setOpciones(opciones);

        q.setRespuestaCorrecta(this.respuestaCorrecta);
        q.setJustificacion(this.justificacion);
        q.setBibliografia(this.bibliografia);
        q.setCompetencia(this.competencia);
        q.setTema(this.tema);
        q.setSubtema(this.subtema);
        q.setNivelDificultad(this.nivelDificultad);
        q.setEstado(this.estado != null ? this.estado : EstadoPregunta.BORRADOR);
        q.setAutorId(this.autorId);
        return q;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getContexto() { return contexto; }
    public void setContexto(String contexto) { this.contexto = contexto; }
    public String getEnunciado() { return enunciado; }
    public void setEnunciado(String enunciado) { this.enunciado = enunciado; }
    public String getOpcionA() { return opcionA; }
    public void setOpcionA(String opcionA) { this.opcionA = opcionA; }
    public String getOpcionB() { return opcionB; }
    public void setOpcionB(String opcionB) { this.opcionB = opcionB; }
    public String getOpcionC() { return opcionC; }
    public void setOpcionC(String opcionC) { this.opcionC = opcionC; }
    public String getOpcionD() { return opcionD; }
    public void setOpcionD(String opcionD) { this.opcionD = opcionD; }
    public String getRespuestaCorrecta() { return respuestaCorrecta; }
    public void setRespuestaCorrecta(String respuestaCorrecta) { this.respuestaCorrecta = respuestaCorrecta; }
    public String getJustificacion() { return justificacion; }
    public void setJustificacion(String justificacion) { this.justificacion = justificacion; }
    public String getBibliografia() { return bibliografia; }
    public void setBibliografia(String bibliografia) { this.bibliografia = bibliografia; }
    public String getCompetencia() { return competencia; }
    public void setCompetencia(String competencia) { this.competencia = competencia; }
    public String getTema() { return tema; }
    public void setTema(String tema) { this.tema = tema; }
    public String getSubtema() { return subtema; }
    public void setSubtema(String subtema) { this.subtema = subtema; }
    public NivelDificultad getNivelDificultad() { return nivelDificultad; }
    public void setNivelDificultad(NivelDificultad nivelDificultad) { this.nivelDificultad = nivelDificultad; }
    public EstadoPregunta getEstado() { return estado; }
    public void setEstado(EstadoPregunta estado) { this.estado = estado; }
    public String getEstadoHex() { return estadoHex; }
    public void setEstadoHex(String estadoHex) { this.estadoHex = estadoHex; }
    public String getAutorId() { return autorId; }
    public void setAutorId(String autorId) { this.autorId = autorId; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
    public void setFechaModificacion(LocalDateTime fechaModificacion) { this.fechaModificacion = fechaModificacion; }
}
