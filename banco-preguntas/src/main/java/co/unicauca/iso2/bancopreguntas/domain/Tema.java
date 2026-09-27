/**
 * @file Tema.java
 * @brief Tema que pertenece a una competencia.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.domain;

import java.util.Objects;

/**
 * @brief Entidad de catálogo: tema dentro de una Competencia.
 */
public class Tema {

    private String id;
    private String nombre;
    private String competenciaId;

    public Tema() {
    }

    public Tema(String id, String nombre, String competenciaId) {
        this.id = id;
        this.nombre = nombre;
        this.competenciaId = competenciaId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCompetenciaId() { return competenciaId; }
    public void setCompetenciaId(String competenciaId) {
        this.competenciaId = competenciaId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tema tema)) return false;
        return Objects.equals(id, tema.id);
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
