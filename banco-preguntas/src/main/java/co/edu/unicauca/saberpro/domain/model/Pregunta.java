/**
 * @file Pregunta.java
 * @brief Raíz de Agregado (Aggregate Root) del contexto de autoría y preguntas Saber Pro.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.edu.unicauca.saberpro.domain.model;

import co.edu.unicauca.saberpro.domain.exception.ReglaNegocioException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @brief Raíz de Agregado que gobierna el ciclo de vida, opciones y reglas de consistencia de una pregunta.
 *
 * Encapsula la invariante de selección múltiple con única respuesta según los lineamientos
 * del Diseño Centrado en Evidencia (DCE) y el taller de Domain-Driven Design (DDD).
 */
public class Pregunta {

    private Long id;
    private String autorId;
    private Enunciado enunciado;
    private Justificacion justificacion;
    private final List<OpcionRespuesta> opciones = new ArrayList<>();
    private EstadoPregunta estado = EstadoPregunta.BORRADOR;

    /**
     * @brief Constructor por defecto. Inicializa la pregunta en estado BORRADOR.
     */
    public Pregunta() {
        this.estado = EstadoPregunta.BORRADOR;
    }

    /**
     * @brief Constructor con datos esenciales.
     * @param id identificador único del agregado.
     * @param autorId identificador del autor docente.
     * @param enunciado enunciado inicial.
     * @param justificacion justificación de la clave.
     */
    public Pregunta(Long id, String autorId, Enunciado enunciado, Justificacion justificacion) {
        this.id = id;
        this.autorId = autorId;
        this.enunciado = enunciado;
        this.justificacion = justificacion;
        this.estado = EstadoPregunta.BORRADOR;
    }

    /**
     * @brief Agrega una opción de respuesta al agregado validando invariantes de negocio.
     * @param texto texto descriptivo de la opción.
     * @param esCorrecta indica si la opción es la clave de respuesta.
     * @throws ReglaNegocioException si la pregunta no está en BORRADOR o si supera las 4 opciones.
     */
    public void agregarOpcion(String texto, boolean esCorrecta) {
        if (this.estado != EstadoPregunta.BORRADOR) {
            throw new ReglaNegocioException("Solo se pueden modificar opciones en estado Borrador.");
        }
        if (this.opciones.size() >= 4) {
            throw new ReglaNegocioException("Una pregunta no puede tener más de 4 opciones.");
        }
        this.opciones.add(new OpcionRespuesta(texto, esCorrecta));
    }

    /**
     * @brief Transiciona la pregunta de BORRADOR a PENDIENTE_REVISION verificando todas las invariantes.
     * @throws ReglaNegocioException si no cumple con las condiciones pedagógicas y estructurales.
     */
    public void enviarARevision() {
        if (this.estado != EstadoPregunta.BORRADOR) {
            throw new ReglaNegocioException("Solo una pregunta en estado Borrador puede ser enviada a revisión.");
        }
        validarInvariantesParaEnvio();
        this.estado = EstadoPregunta.PENDIENTE_REVISION;
    }

    /**
     * @brief Valida que la pregunta cumpla con las invariantes estrictas para ser evaluada por pares.
     */
    private void validarInvariantesParaEnvio() {
        if (this.enunciado == null) {
            throw new ReglaNegocioException("La pregunta debe contener un enunciado válido antes de ser enviada.");
        }
        if (this.justificacion == null) {
            throw new ReglaNegocioException("La pregunta debe contener una justificación válida antes de ser enviada.");
        }
        if (this.opciones.size() != 4) {
            throw new ReglaNegocioException("Debe tener exactamente 4 opciones de respuesta.");
        }
        long correctas = opciones.stream().filter(OpcionRespuesta::isEsCorrecta).count();
        if (correctas != 1) {
            throw new ReglaNegocioException("Debe existir exactamente 1 opción correcta.");
        }
    }

    /**
     * @brief Inicia el proceso de revisión por pares.
     * @throws ReglaNegocioException si la pregunta no se encontraba en PENDIENTE_REVISION.
     */
    public void iniciarRevision() {
        if (this.estado != EstadoPregunta.PENDIENTE_REVISION) {
            throw new ReglaNegocioException("Solo una pregunta pendiente de revisión puede pasar a En Revisión.");
        }
        this.estado = EstadoPregunta.EN_REVISION;
    }

    /**
     * @brief Dictamina la aprobación de la pregunta tras revisión de pares.
     * @throws ReglaNegocioException si la pregunta no estaba en revisión.
     */
    public void aprobar() {
        if (this.estado != EstadoPregunta.EN_REVISION) {
            throw new ReglaNegocioException("Solo una pregunta en revisión puede ser aprobada.");
        }
        this.estado = EstadoPregunta.APROBADA;
    }

    /**
     * @brief Dictamina el rechazo de la pregunta tras revisión de pares.
     * @param motivo explicación del rechazo.
     * @throws ReglaNegocioException si la pregunta no estaba en revisión o el motivo es vacío.
     */
    public void rechazar(String motivo) {
        if (this.estado != EstadoPregunta.EN_REVISION) {
            throw new ReglaNegocioException("Solo una pregunta en revisión puede ser rechazada.");
        }
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new ReglaNegocioException("El rechazo debe fundamentarse en un motivo explícito.");
        }
        this.estado = EstadoPregunta.RECHAZADA;
    }

    // Getters y Setters de encapsulación segura

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAutorId() {
        return autorId;
    }

    public void setAutorId(String autorId) {
        this.autorId = autorId;
    }

    public Enunciado getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(Enunciado enunciado) {
        if (this.estado != EstadoPregunta.BORRADOR) {
            throw new ReglaNegocioException("Solo se puede cambiar el enunciado en estado Borrador.");
        }
        this.enunciado = enunciado;
    }

    public Justificacion getJustificacion() {
        return justificacion;
    }

    public void setJustificacion(Justificacion justificacion) {
        if (this.estado != EstadoPregunta.BORRADOR) {
            throw new ReglaNegocioException("Solo se puede cambiar la justificación en estado Borrador.");
        }
        this.justificacion = justificacion;
    }

    public List<OpcionRespuesta> getOpciones() {
        return Collections.unmodifiableList(opciones);
    }

    public EstadoPregunta getEstado() {
        return estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pregunta pregunta = (Pregunta) o;
        return Objects.equals(id, pregunta.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
