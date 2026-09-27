/**
 * @file Competencia.java
 * @brief Competencia evaluada en las pruebas Saber Pro.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.domain;

import java.util.Objects;

/**
 * @brief Entidad de catálogo: competencia (ej. "Lectura crítica").
 */
public class Competencia {

    private String id;
    private String nombre;

    public Competencia() {
    }

    public Competencia(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Competencia that)) return false;
        return Objects.equals(id, that.id);
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
