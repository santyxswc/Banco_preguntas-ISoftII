/**
 * @file BancoPreguntasFacade.java
 * @brief Patrón GoF Estructural: Facade para unificar y simplificar el subsistema de banco de preguntas.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.domain.facade;

import co.unicauca.iso2.bancopreguntas.domain.Asignacion;
import co.unicauca.iso2.bancopreguntas.domain.AsignacionService;
import co.unicauca.iso2.bancopreguntas.domain.EstadoPregunta;
import co.unicauca.iso2.bancopreguntas.domain.PaginaResultado;
import co.unicauca.iso2.bancopreguntas.domain.Question;
import co.unicauca.iso2.bancopreguntas.domain.QuestionFilter;
import co.unicauca.iso2.bancopreguntas.domain.QuestionService;
import co.unicauca.iso2.bancopreguntas.domain.Revision;
import co.unicauca.iso2.bancopreguntas.domain.RevisionService;
import co.unicauca.iso2.bancopreguntas.domain.Usuario;
import co.unicauca.iso2.bancopreguntas.domain.UsuarioRepository;
import co.unicauca.iso2.bancopreguntas.domain.validacion.ValidadorEstructuralPregunta;

import java.util.List;
import java.util.Objects;

/**
 * @brief Fachada principal del dominio del Banco de Preguntas.
 *
 * Oculta la complejidad de orquestación entre múltiples servicios de dominio
 * (QuestionService, AsignacionService, RevisionService, ValidadorEstructural)
 * ofreciendo una interfaz de alto nivel clara y cohesiva.
 */
public class BancoPreguntasFacade {

    private final QuestionService questionService;
    private final AsignacionService asignacionService;
    private final RevisionService revisionService;
    private final UsuarioRepository usuarioRepository;
    private final ValidadorEstructuralPregunta validador;

    public BancoPreguntasFacade(QuestionService questionService,
                                AsignacionService asignacionService,
                                RevisionService revisionService,
                                UsuarioRepository usuarioRepository) {
        this.questionService = Objects.requireNonNull(questionService, "QuestionService es obligatorio");
        this.asignacionService = Objects.requireNonNull(asignacionService, "AsignacionService es obligatorio");
        this.revisionService = Objects.requireNonNull(revisionService, "RevisionService es obligatorio");
        this.usuarioRepository = Objects.requireNonNull(usuarioRepository, "UsuarioRepository es obligatorio");
        this.validador = new ValidadorEstructuralPregunta();
    }

    // ==========================================
    // HU01, HU02, HU03: Operaciones de Pregunta (Autor)
    // ==========================================

    /**
     * @brief Valida y guarda una nueva pregunta (HU01).
     * @param question pregunta a guardar
     * @return lista de errores de validación (vacía si es exitosa)
     */
    public List<String> crearPregunta(Question question) {
        List<String> errores = validador.validar(question);
        if (!errores.isEmpty()) {
            return errores;
        }
        boolean guardado = questionService.saveQuestion(question);
        if (!guardado) {
            errores.add("No fue posible persistir la pregunta.");
        }
        return errores;
    }

    /**
     * @brief Valida estructuralmente una pregunta sin guardarla.
     * @param question pregunta a verificar
     * @return lista de errores
     */
    public List<String> validarEstructuralmente(Question question) {
        return validador.validar(question);
    }

    /**
     * @brief Cambia el estado de una pregunta (HU02).
     * @param preguntaId id de la pregunta
     * @param nuevoEstado estado destino
     * @return true si la transición fue exitosa
     */
    public boolean cambiarEstado(String preguntaId, EstadoPregunta nuevoEstado) {
        return questionService.updateEstado(preguntaId, nuevoEstado);
    }

    /**
     * @brief Obtiene una pregunta por su ID.
     */
    public Question obtenerPreguntaPorId(String id) {
        return questionService.findQuestionById(id);
    }

    /**
     * @brief Lista preguntas con filtros y paginación (HU03).
     */
    public PaginaResultado<Question> listarPreguntas(QuestionFilter filtro, int pagina, int tamano) {
        return questionService.listQuestions(filtro, pagina, tamano);
    }

    /**
     * @brief Lista todas las preguntas de un autor (HU03).
     */
    public List<Question> listarPorAutor(String autorId) {
        return questionService.listQuestionsByAutor(autorId);
    }

    // ==========================================
    // HU04: Operaciones de Asignación (Administrador)
    // ==========================================

    /**
     * @brief Asigna entre 1 y 3 revisores a una pregunta pendiente (HU04).
     * @param preguntaId id de la pregunta
     * @param revisorIds lista de ids de revisores
     * @return lista de errores (vacía si es exitosa)
     */
    public List<String> asignarRevisores(String preguntaId, List<String> revisorIds) {
        return asignacionService.asignarRevisores(preguntaId, revisorIds);
    }

    /**
     * @brief Lista preguntas en estado PENDIENTE_REVISION listas para asignar.
     */
    public List<Question> listarPreguntasPendientesRevision() {
        return asignacionService.listarPreguntasPendientes();
    }

    // ==========================================
    // HU05: Operaciones de Revisión por Pares (Revisor)
    // ==========================================

    /**
     * @brief Lista las preguntas asignadas a un revisor en estado EN_REVISION (HU05).
     * @param revisorId id del revisor
     * @return lista de preguntas asignadas
     */
    public List<Question> listarPreguntasAsignadas(String revisorId) {
        return revisionService.listarPreguntasAsignadas(revisorId);
    }

    /**
     * @brief Registra la evaluación de un revisor (APROBADA/RECHAZADA) con observaciones (HU05).
     * @param preguntaId id de la pregunta
     * @param revisorId id del revisor
     * @param veredicto APROBADA o RECHAZADA
     * @param observaciones comentarios para el autor
     * @return lista de errores (vacía si es exitosa)
     */
    public List<String> evaluarPregunta(String preguntaId, String revisorId,
                                       EstadoPregunta veredicto, String observaciones) {
        return revisionService.evaluar(preguntaId, revisorId, veredicto, observaciones);
    }

    /**
     * @brief Obtiene el historial de revisiones de una pregunta.
     */
    public List<Revision> obtenerHistorialRevisiones(String preguntaId) {
        return revisionService.historialDe(preguntaId);
    }

    // ==========================================
    // Consultas de Usuarios
    // ==========================================

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.list();
    }

    public Usuario obtenerUsuarioPorId(String id) {
        return usuarioRepository.findById(id);
    }
}
