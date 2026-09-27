/**
 * @file QuestionDistractors.java
 * @brief Opción de respuesta de una pregunta.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.domain;

/**
 * @brief Una de las opciones (A, B, C, D) de una pregunta.
 *
 * La opción cuyo id coincide con Question#getRespuestaCorrecta() es la
 * clave; las demás son distractores.
 */
public class QuestionDistractors {

    private String id;
    private String texto;

    public QuestionDistractors() {
    }

    /**
     * @param id    letra de la opción
     * @param texto texto de la opción
     */
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
