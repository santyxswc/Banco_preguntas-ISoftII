/**
 * @file Subtema.java
 * @brief Subtema que pertenece a un tema.
 * @author Santiago Caicedo
 */
package co.unicauca.iso2.bancopreguntas.domain;

import java.util.Objects;

/**
 * @brief Entidad de catálogo: subtema dentro de un Tema.
 */
public class Subtema {

    private String id;
    private String nombre;
    private String temaId;

    public Subtema() {
    }

    public Subtema(String id, String nombre, String temaId) {
        this.id = id;
        this.nombre = nombre;
        this.temaId = temaId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTemaId() { return temaId; }
    public void setTemaId(String temaId) { this.temaId = temaId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subtema subtema)) return false;
        return Objects.equals(id, subtema.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return nombre;
    }
}
