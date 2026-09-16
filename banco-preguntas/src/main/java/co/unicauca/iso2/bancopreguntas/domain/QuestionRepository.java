package co.unicauca.iso2.bancopreguntas.domain;

import java.util.List;

/**
 * Abstracción para la persistencia de preguntas.
 *
 * QuestionService depende de esta interfaz (capa de dominio) y no de
 * una implementación concreta como QuestionImplRepository (capa de
 * acceso a datos). Esto aplica el Principio de Inversión de
 * Dependencias (DIP):
 *
 * QuestionService
 *       ↓
 * QuestionRepository
 *       ↑
 * QuestionImplRepository
 */
public interface QuestionRepository {

    /**
     * Obtiene todas las preguntas del banco.
     *
     * @return lista de preguntas
     */
    List<Question> list();

    /**
     * Busca una pregunta por su identificador.
     *
     * @param id identificador de la pregunta
     * @return pregunta encontrada o null si no existe
     */
    Question findById(String id);

    /**
     * Obtiene las preguntas creadas por un autor específico (RF03.1).
     *
     * @param autorId identificador del autor
     * @return lista de preguntas del autor
     */
    List<Question> findByAutorId(String autorId);

    /**
     * Obtiene las preguntas que se encuentran en un estado específico.
     * Usado por el administrador para ver las pendientes de revisión
     * (RF04.1).
     *
     * @param estado estado a filtrar
     * @return lista de preguntas en ese estado
     */
    List<Question> findByEstado(EstadoPregunta estado);

    /**
     * Guarda una nueva pregunta en el banco.
     *
     * @param question pregunta a guardar
     * @return true si fue almacenada correctamente
     */
    boolean save(Question question);

    /**
     * Actualiza una pregunta existente (todos sus campos).
     * Usado al editar una pregunta en BORRADOR.
     *
     * @param question pregunta con los datos actualizados
     * @return true si la pregunta existía y fue actualizada
     */
    boolean update(Question question);

    /**
     * Actualiza el estado de una pregunta existente.
     *
     * @param id          identificador de la pregunta
     * @param nuevoEstado nuevo estado de la pregunta
     * @return true si la pregunta existía y fue actualizada
     */
    boolean updateEstado(String id, EstadoPregunta nuevoEstado);
}
