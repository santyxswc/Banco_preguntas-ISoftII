/**
 * @file QuestionService.java
 * @brief Lógica de negocio de las preguntas del banco.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.domain;

import co.unicauca.iso2.bancopreguntas.domain.validacion.ReglaValidacion;
import co.unicauca.iso2.bancopreguntas.domain.validacion.ValidadorEstructuralPregunta;
import co.unicauca.iso2.bancopreguntas.infra.Subject;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @brief Servicio de preguntas: consultas, creación, edición y cambios
 *        de estado.
 *
 * Recibe sus dependencias por constructor (repositorios y regla de
 * validación) y extiende Subject para avisar a las vistas cada vez que
 * el banco cambia.
 */
public class QuestionService extends Subject {

    private final QuestionRepository questionRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReglaValidacion validador;

    /** @param questionRepository repositorio de preguntas */
    public QuestionService(QuestionRepository questionRepository) {
        this(questionRepository, null);
    }

    /**
     * @param questionRepository repositorio de preguntas
     * @param usuarioRepository  repositorio de usuarios
     */
    public QuestionService(QuestionRepository questionRepository,
            UsuarioRepository usuarioRepository) {
        this(questionRepository, usuarioRepository,
                new ValidadorEstructuralPregunta());
    }

    /**
     * @param questionRepository repositorio de preguntas
     * @param usuarioRepository  repositorio de usuarios
     * @param validador          regla de validación estructural
     */
    public QuestionService(QuestionRepository questionRepository,
            UsuarioRepository usuarioRepository,
            ReglaValidacion validador) {
        this.questionRepository = questionRepository;
        this.usuarioRepository = usuarioRepository;
        this.validador = validador != null
                ? validador : new ValidadorEstructuralPregunta();
    }

    /** @return todas las preguntas del banco */
    public List<Question> listQuestions() {
        return questionRepository.list();
    }

    /**
     * @param autorId identificador del autor
     * @return preguntas del autor (vacía si el id no es válido)
     */
    public List<Question> listByAutor(String autorId) {
        if (esVacio(autorId)) {
            return new ArrayList<>();
        }
        return questionRepository.findByAutorId(autorId);
    }

    /**
     * @param estado estado a filtrar
     * @return preguntas en ese estado
     */
    public List<Question> listByEstado(EstadoPregunta estado) {
        if (estado == null) {
            return new ArrayList<>();
        }
        return questionRepository.findByEstado(estado);
    }

    /**
     * @brief Búsqueda paginada con filtros.
     * @param filtro criterios de búsqueda, orden y paginación
     * @return página de preguntas
     */
    public PaginaResultado<Question> buscarPaginado(QuestionFilter filtro) {
        return questionRepository.buscar(filtro);
    }

    /**
     * @param id identificador de la pregunta
     * @return la pregunta o null
     */
    public Question findQuestionById(String id) {
        if (esVacio(id)) {
            return null;
        }
        return questionRepository.findById(id);
    }

    /**
     * @brief Docentes que pueden ser asignados como revisores.
     * @return usuarios con rol AUTOR
     */
    public List<Usuario> listarRevisoresDisponibles() {
        if (usuarioRepository == null) {
            return new ArrayList<>();
        }
        return usuarioRepository.findByRol(Rol.AUTOR);
    }

    /**
     * @brief Aplica la validación estructural a una pregunta.
     * @param q pregunta a validar
     * @return errores en formato "campo|mensaje" (vacía si es válida)
     */
    public List<String> validarEstructura(Question q) {
        return validador.validar(q);
    }

    /**
     * @brief Registra una pregunta nueva.
     *
     * La pregunta debe pasar la validación estructural. Si no trae
     * estado queda en BORRADOR.
     *
     * @param question pregunta a registrar
     * @return true si se guardó
     */
    public boolean saveQuestion(Question question) {

        if (question == null || esVacio(question.getId())) {
            return false;
        }

        if (!validarEstructura(question).isEmpty()) {
            return false;
        }

        if (question.getEstado() == null) {
            question.setEstado(EstadoPregunta.BORRADOR);
        }

        boolean guardada = questionRepository.save(question);

        if (guardada) {
            notifyObservers();
        }

        return guardada;
    }

    /**
     * @brief Actualiza una pregunta que sigue en BORRADOR.
     * @param question pregunta con los datos nuevos
     * @return true si se actualizó
     */
    public boolean updateQuestion(Question question) {

        if (question == null || esVacio(question.getId())) {
            return false;
        }

        Question existente = questionRepository.findById(question.getId());
        if (existente == null || existente.getEstado() == null
                || !existente.getEstado().permiteEdicion()) {
            return false;
        }

        if (!validarEstructura(question).isEmpty()) {
            return false;
        }

        boolean actualizada = questionRepository.update(question);

        if (actualizada) {
            notifyObservers();
        }

        return actualizada;
    }

    /**
     * @brief Pasa una pregunta de BORRADOR a PENDIENTE_REVISION.
     *
     * Solo su autor puede hacerlo y la pregunta debe estar completa.
     *
     * @param preguntaId id de la pregunta
     * @param autorId    id del usuario que lo solicita
     * @return errores encontrados (vacía si el cambio se hizo)
     */
    public List<String> enviarARevision(String preguntaId, String autorId) {

        List<String> errores = new ArrayList<>();

        Question pregunta = findQuestionById(preguntaId);

        if (pregunta == null) {
            errores.add("La pregunta no existe.");
            return errores;
        }

        if (esVacio(autorId) || !Objects.equals(autorId, pregunta.getAutorId())) {
            errores.add("Solo el autor de la pregunta puede enviarla a revisión.");
            return errores;
        }

        if (pregunta.getEstado() == null
                || !pregunta.getEstado().permiteEnviarARevision()) {
            errores.add("Solo se pueden enviar a revisión preguntas en borrador.");
            return errores;
        }

        if (!validarEstructura(pregunta).isEmpty()) {
            errores.add("La pregunta no pasa la validación estructural. "
                    + "Completa todos los campos antes de enviarla.");
            return errores;
        }

        if (!updateEstado(preguntaId, EstadoPregunta.PENDIENTE_REVISION)) {
            errores.add("No fue posible actualizar el estado de la pregunta.");
        }

        return errores;
    }

    /**
     * @brief Cambia el estado de una pregunta respetando las
     *        transiciones permitidas y avisa a los observadores.
     * @param id          id de la pregunta
     * @param nuevoEstado estado destino
     * @return true si el cambio se realizó
     */
    public boolean updateEstado(String id, EstadoPregunta nuevoEstado) {

        if (esVacio(id) || nuevoEstado == null) {
            return false;
        }

        Question pregunta = questionRepository.findById(id);
        if (pregunta == null || pregunta.getEstado() == null
                || !pregunta.getEstado().puedeCambiarA(nuevoEstado)) {
            return false;
        }

        boolean actualizada = questionRepository.updateEstado(id, nuevoEstado);

        if (actualizada) {
            notifyObservers();
        }

        return actualizada;
    }

    /**
     * @brief Cantidad de preguntas en cada estado.
     * @return mapa estado → cantidad
     */
    public Map<EstadoPregunta, Long> contarPorEstado() {

        Map<EstadoPregunta, Long> conteo = new EnumMap<>(EstadoPregunta.class);

        for (EstadoPregunta estado : EstadoPregunta.values()) {
            conteo.put(estado, 0L);
        }

        for (Question pregunta : questionRepository.list()) {
            if (pregunta.getEstado() != null) {
                conteo.merge(pregunta.getEstado(), 1L, Long::sum);
            }
        }

        return conteo;
    }

    /**
     * @brief Porcentaje de preguntas en cada estado.
     * @return mapa estado → porcentaje (0 a 100)
     */
    public Map<EstadoPregunta, Double> porcentajePorEstado() {

        Map<EstadoPregunta, Long> conteo = contarPorEstado();

        long total = 0;
        for (long cantidad : conteo.values()) {
            total += cantidad;
        }

        Map<EstadoPregunta, Double> porcentajes = new EnumMap<>(EstadoPregunta.class);

        for (Map.Entry<EstadoPregunta, Long> entrada : conteo.entrySet()) {
            double porcentaje = total == 0
                    ? 0.0
                    : (entrada.getValue() * 100.0) / total;
            porcentajes.put(entrada.getKey(), porcentaje);
        }

        return porcentajes;
    }

    private boolean esVacio(String s) {
        return s == null || s.isBlank();
    }
}
