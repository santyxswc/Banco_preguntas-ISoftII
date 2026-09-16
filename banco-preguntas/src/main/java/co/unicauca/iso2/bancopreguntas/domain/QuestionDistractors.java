package co.unicauca.iso2.bancopreguntas.domain;

/**
 * Representa una de las opciones de respuesta de una pregunta de
 * selección múltiple (A, B, C, D...).
 *
 * Una {@link Question} tiene varias QuestionDistractors: una de ellas
 * es la respuesta correcta (identificada mediante
 * {@link Question#getRespuestaCorrecta()}) y las demás son
 * distractores.
 */
public class QuestionDistractors {

    private String id;
    private String texto;

    public QuestionDistractors() {
    }

    public QuestionDistractors(String id, String texto) {
        this.id = id;
        this.texto = texto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    @Override
    public String toString() {
        return id + ". " + texto;
    }
}
