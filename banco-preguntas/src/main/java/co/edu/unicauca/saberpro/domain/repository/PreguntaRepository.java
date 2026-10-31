/**
 * @file PreguntaRepository.java
 * @brief Contrato puro de repositorio de dominio para el Agregado Pregunta (DDD).
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.edu.unicauca.saberpro.domain.repository;

import co.edu.unicauca.saberpro.domain.model.EstadoPregunta;
import co.edu.unicauca.saberpro.domain.model.Pregunta;

import java.util.List;
import java.util.Optional;

/**
 * @brief Interfaz pura de repositorio independiente de cualquier tecnología de persistencia.
 *
 * Expresa las operaciones de persistencia del agregado Pregunta en términos del lenguaje ubicuo.
 */
public interface PreguntaRepository {

    /**
     * @brief Guarda o actualiza el agregado Pregunta en el almacenamiento.
     * @param pregunta instancia del agregado a persistir.
     * @return la instancia persistida.
     */
    Pregunta save(Pregunta pregunta);

    /**
     * @brief Busca una pregunta por su identificador único.
     * @param id identificador del agregado.
     * @return Optional con la pregunta si existe.
     */
    Optional<Pregunta> findById(Long id);

    /**
     * @brief Retorna todas las preguntas registradas en el dominio.
     * @return lista inmutable o completa de preguntas.
     */
    List<Pregunta> findAll();

    /**
     * @brief Filtra preguntas por su estado dentro del ciclo de vida.
     * @param estado estado del ciclo de vida a consultar.
     * @return lista de preguntas en dicho estado.
     */
    List<Pregunta> findByEstado(EstadoPregunta estado);

    /**
     * @brief Elimina una pregunta por su identificador.
     * @param id identificador de la pregunta.
     */
    void deleteById(Long id);
}
