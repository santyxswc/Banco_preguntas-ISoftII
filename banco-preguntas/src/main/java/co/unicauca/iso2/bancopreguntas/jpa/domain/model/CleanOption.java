/**
 * @file CleanOption.java
 * @brief Entidad u objeto de dominio puro para opciones de respuesta (Taller 10 - JPA).
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.jpa.domain.model;

import java.util.Objects;

/**
 * @brief Opción de respuesta en el modelo de dominio puro sin contaminación de persistencia.
 */
public class CleanOption {

    private Long id;
    private String text;
    private boolean isCorrect;

    public CleanOption() {}

    public CleanOption(Long id, String text, boolean isCorrect) {
        this.id = id;
        this.text = text;
        this.isCorrect = isCorrect;
    }

    public CleanOption(String text, boolean isCorrect) {
        this(null, text, isCorrect);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CleanOption that = (CleanOption) o;
        return isCorrect == that.isCorrect && Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, isCorrect);
    }
}
