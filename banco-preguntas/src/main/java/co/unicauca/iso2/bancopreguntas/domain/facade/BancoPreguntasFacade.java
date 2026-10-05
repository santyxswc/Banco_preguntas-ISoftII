/**
 * @file BancoPreguntasFacade.java
 * @brief Patrón GoF Estructural: Facade para unificar y simplificar el subsistema de banco de preguntas.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.domain.facade;

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

import java.util.ArrayList;
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

    public List<String> validarEstructuralmente(Question question) {
        return validador.validar(question);
    }

    public boolean cambiarEstado(String preguntaId, EstadoPregunta nuevoEstado) {
        return questionService.updateEstado(preguntaId, nuevoEstado);
    }

    public Question obtenerPreguntaPorId(String id) {
        return questionService.findQuestionById(id);
    }

    public PaginaResultado<Question> listarPreguntas(QuestionFilter filtro) {
        return questionService.buscarPaginado(filtro);
    }

    public List<Question> listarPorAutor(String autorId) {
        return questionService.listByAutor(autorId);
    }

    // ==========================================
    // HU04: Operaciones de Asignación (Administrador)
    // ==========================================

    public List<String> asignarRevisores(String preguntaId, List<String> revisorIds) {
        List<String> errores = new ArrayList<>();
        if (revisorIds == null || revisorIds.isEmpty() || revisorIds.size() > 3) {
            errores.add("Debe asignar entre 1 y 3 revisores.");
            return errores;
        }

        boolean exito = asignacionService.asignarRevisores(preguntaId, revisorIds);
        if (!exito) {
            errores.add("No se pudo realizar la asignación. Verifique que la pregunta esté en estado PENDIENTE_REVISION y que el autor no sea uno de los revisores.");
        }
        return errores;
    }

    public List<Question> listarPreguntasPendientesRevision() {
        return asignacionService.listarPreguntasPendientes();
    }

    // ==========================================
    // HU05: Operaciones de Revisión por Pares (Revisor)
    // ==========================================

    public List<Question> listarPreguntasAsignadas(String revisorId) {
        return revisionService.listarPreguntasAsignadas(revisorId);
    }

    public List<String> evaluarPregunta(String preguntaId, String revisorId,
                                       EstadoPregunta veredicto, String observaciones) {
        return revisionService.evaluar(preguntaId, revisorId, veredicto, observaciones);
    }

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
