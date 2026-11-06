/**
 * @file Revisor.java
 * @brief Entidad (Entity) del contexto de Revisión por Pares: el docente que evalúa una pregunta.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 */
package co.edu.unicauca.saberpro.domain.model;

import co.edu.unicauca.saberpro.domain.exception.ReglaNegocioException;

import java.util.Objects;

/**
 * @brief Entidad con identidad propia: el ID del docente.
 *
 * Dos revisores son el mismo si tienen el mismo ID de docente, aunque cambie su nombre.
 * Es la diferencia con un Value Object, que se compara por todos sus atributos.
 */
public class Revisor {

    private final String idDocente;
    private String nombre;

    /**
     * @brief Crea un revisor identificado por su ID de docente.
     * @param idDocente identificador único del docente (no vacío).
     * @param nombre nombre visible del docente (no vacío).
     * @throws ReglaNegocioException si el ID o el nombre están vacíos.
     */
    public Revisor(String idDocente, String nombre) {
        if (idDocente == null || idDocente.isBlank()) {
            throw new ReglaNegocioException("El revisor debe tener un ID de docente.");
        }
        this.idDocente = idDocente.trim();
        cambiarNombre(nombre);
    }

    /**
     * @brief Cambia el nombre visible; la identidad (ID de docente) no cambia.
     * @param nombre nuevo nombre (no vacío).
     */
    public void cambiarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new ReglaNegocioException("El revisor debe tener un nombre.");
        }
        this.nombre = nombre.trim();
    }

    public String getIdDocente() {
        return idDocente;
    }

    public String getNombre() {
        return nombre;
    }

    /** @brief Igualdad por identidad: solo cuenta el ID de docente. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Revisor)) return false;
        return idDocente.equals(((Revisor) o).idDocente);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idDocente);
    }

    @Override
    public String toString() {
        return "Revisor{" + idDocente + ", " + nombre + "}";
    }
}
