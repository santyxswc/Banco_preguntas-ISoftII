/**
 * @file QuestionRepository.java
 * @brief Contrato de persistencia de preguntas.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @brief Abstracción de persistencia para Question.
 *
 * QuestionService depende de esta interfaz y no de una implementación
 * concreta (en memoria o JDBC), de modo que el almacenamiento se puede
 * cambiar sin tocar la lógica de negocio.
 */
public interface QuestionRepository {

    /** @return todas las preguntas del banco */
    List<Question> list();

    /**
     * @param id identificador de la pregunta
     * @return la pregunta o null si no existe
     */
    Question findById(String id);

    /**
     * @param autorId identificador del autor
     * @return preguntas creadas por ese autor
     */
    List<Question> findByAutorId(String autorId);

    /**
     * @param estado estado a filtrar
     * @return preguntas en ese estado
     */
    List<Question> findByEstado(EstadoPregunta estado);

    /**
     * @brief Guarda una pregunta nueva.
     * @param question pregunta a guardar
     * @return true si se guardó; false si el id ya existe
     */
    boolean save(Question question);

    /**
     * @brief Reemplaza los datos de una pregunta existente.
     * @param question pregunta con los datos nuevos
     * @return true si la pregunta existía y se actualizó
     */
    boolean update(Question question);

    /**
     * @brief Cambia solo el estado de una pregunta.
     * @param id          identificador de la pregunta
     * @param nuevoEstado estado nuevo
     * @return true si la pregunta existía y se actualizó
     */
    boolean updateEstado(String id, EstadoPregunta nuevoEstado);

    /**
     * @brief Búsqueda con filtros, orden y paginación.
     *
     * La implementación por defecto filtra en memoria sobre list();
     * un repositorio con base de datos puede sobrescribirla para
     * resolverlo con SQL.
     *
     * @param filtro criterios de búsqueda
     * @return página de resultados
     */
    default PaginaResultado<Question> buscar(QuestionFilter filtro) {

        QuestionFilter f = filtro != null ? filtro : new QuestionFilter();

        List<Question> base = (f.getAutorId() != null && !f.getAutorId().isBlank())
                ? findByAutorId(f.getAutorId())
                : list();

        List<Question> filtradas = new ArrayList<>();
        for (Question q : base) {
            if (cumpleFiltro(q, f)) {
                filtradas.add(q);
            }
        }

        filtradas.sort(comparadorPara(f.getOrden()));

        int total = filtradas.size();
        int tam = Math.max(1, f.getTamPagina());
        int desde = Math.max(0, f.getPagina()) * tam;
        int hasta = Math.min(desde + tam, total);

        List<Question> pagina = (desde < total)
                ? new ArrayList<>(filtradas.subList(desde, hasta))
                : new ArrayList<>();

        return new PaginaResultado<>(pagina, total, f.getPagina(), tam);
    }

    private boolean cumpleFiltro(Question q, QuestionFilter f) {

        if (f.getEstado() != null && q.getEstado() != f.getEstado()) {
            return false;
        }
        if (f.getCompetencia() != null && !f.getCompetencia().isBlank()
                && (q.getCompetencia() == null || !q.getCompetencia()
                        .toLowerCase().contains(f.getCompetencia().toLowerCase()))) {
            return false;
        }
        if (f.getTema() != null && !f.getTema().isBlank()
                && (q.getTema() == null || !q.getTema()
                        .toLowerCase().contains(f.getTema().toLowerCase()))) {
            return false;
        }
        if (f.getSubtema() != null && !f.getSubtema().isBlank()
                && (q.getSubtema() == null || !q.getSubtema()
                        .toLowerCase().contains(f.getSubtema().toLowerCase()))) {
            return false;
        }
        if (f.getNivelDificultad() != null
                && q.getNivelDificultad() != f.getNivelDificultad()) {
            return false;
        }
        if (f.getTextoLibre() != null && !f.getTextoLibre().isBlank()) {
            String texto = f.getTextoLibre().toLowerCase();
            boolean enEnunciado = q.getEnunciado() != null
                    && q.getEnunciado().toLowerCase().contains(texto);
            boolean enContexto = q.getContexto() != null
                    && q.getContexto().toLowerCase().contains(texto);
            if (!enEnunciado && !enContexto) {
                return false;
            }
        }
        return true;
    }

    private Comparator<Question> comparadorPara(QuestionFilter.Orden orden) {

        Comparator<Question> porFechaCreacion = Comparator.comparing(
                Question::getFechaCreacion,
                Comparator.nullsFirst(Comparator.naturalOrder()));
        Comparator<Question> porFechaModificacion = Comparator.comparing(
                Question::getFechaModificacion,
                Comparator.nullsFirst(Comparator.naturalOrder()));

        if (orden == null) {
            orden = QuestionFilter.Orden.FECHA_CREACION_DESC;
        }

        return switch (orden) {
            case FECHA_CREACION_ASC -> porFechaCreacion;
            case FECHA_CREACION_DESC -> porFechaCreacion.reversed();
            case FECHA_MODIFICACION_ASC -> porFechaModificacion;
            case FECHA_MODIFICACION_DESC -> porFechaModificacion.reversed();
        };
    }
}
