/**
 * @file PreguntaRestController.java
 * @brief Controlador REST para la gestión de preguntas (HU01, HU02, HU03).
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.presentation.rest;

import co.unicauca.iso2.bancopreguntas.domain.EstadoPregunta;
import co.unicauca.iso2.bancopreguntas.domain.NivelDificultad;
import co.unicauca.iso2.bancopreguntas.domain.PaginaResultado;
import co.unicauca.iso2.bancopreguntas.domain.Question;
import co.unicauca.iso2.bancopreguntas.domain.QuestionFilter;
import co.unicauca.iso2.bancopreguntas.domain.facade.BancoPreguntasFacade;
import co.unicauca.iso2.bancopreguntas.presentation.rest.dto.DTOs.ApiResponse;
import co.unicauca.iso2.bancopreguntas.presentation.rest.dto.DTOs.CambioEstadoRequest;
import co.unicauca.iso2.bancopreguntas.presentation.rest.dto.PreguntaDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/preguntas")
@CrossOrigin(origins = "*")
@Tag(name = "Preguntas", description = "Endpoints para la gestión, filtrado, paginación y ciclo de vida de preguntas (HU01, HU02, HU03)")
public class PreguntaRestController {

    private final BancoPreguntasFacade facade;

    public PreguntaRestController(BancoPreguntasFacade facade) {
        this.facade = facade;
    }

    @PostMapping
    @Operation(summary = "Crear nueva pregunta (HU01)", description = "Crea una pregunta en estado BORRADOR aplicando las reglas de validación estructural.")
    public ResponseEntity<ApiResponse<PreguntaDTO>> crearPregunta(@RequestBody PreguntaDTO dto) {
        Question question = dto.toEntity();
        List<String> errores = facade.crearPregunta(question);

        if (!errores.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error(errores, "Error al validar o crear la pregunta"));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(PreguntaDTO.fromEntity(question), "Pregunta creada exitosamente"));
    }

    @PostMapping("/validar")
    @Operation(summary = "Validar estructuralmente una pregunta", description = "Ejecuta las reglas de validación sin persistir la pregunta.")
    public ResponseEntity<ApiResponse<List<String>>> validarPregunta(@RequestBody PreguntaDTO dto) {
        List<String> errores = facade.validarEstructuralmente(dto.toEntity());
        if (errores.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.ok(errores, "La pregunta cumple con todas las reglas estructurales"));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(errores, "Se encontraron errores de validación"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener pregunta por ID", description = "Retorna los detalles completos de una pregunta.")
    public ResponseEntity<ApiResponse<PreguntaDTO>> obtenerPorId(@PathVariable String id) {
        Question q = facade.obtenerPreguntaPorId(id);
        if (q == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(List.of("La pregunta no existe"), "Pregunta no encontrada"));
        }
        return ResponseEntity.ok(ApiResponse.ok(PreguntaDTO.fromEntity(q), "Pregunta encontrada"));
    }

    @GetMapping
    @Operation(summary = "Listar preguntas con filtros y paginación (HU03)", description = "Permite filtrar por autor, estado, tema, dificultad y palabra clave, con soporte para paginación.")
    public ResponseEntity<ApiResponse<PaginaResultado<PreguntaDTO>>> listarPreguntas(
            @RequestParam(required = false) String autorId,
            @RequestParam(required = false) EstadoPregunta estado,
            @RequestParam(required = false) String competencia,
            @RequestParam(required = false) String tema,
            @RequestParam(required = false) String subtema,
            @RequestParam(required = false) NivelDificultad nivelDificultad,
            @RequestParam(required = false) String textoLibre,
            @RequestParam(defaultValue = "1") int pagina,
            @RequestParam(defaultValue = "10") int tamano) {

        QuestionFilter filtro = new QuestionFilter()
                .conAutorId(autorId)
                .conEstado(estado)
                .conCompetencia(competencia)
                .conTema(tema)
                .conSubtema(subtema)
                .conNivelDificultad(nivelDificultad)
                .conTextoLibre(textoLibre)
                .conPagina(Math.max(0, pagina - 1))
                .conTamPagina(tamano);

        PaginaResultado<Question> resultado = facade.listarPreguntas(filtro);

        List<PreguntaDTO> dtos = resultado.getElementos().stream()
                .map(PreguntaDTO::fromEntity)
                .collect(Collectors.toList());

        PaginaResultado<PreguntaDTO> paginaDto = new PaginaResultado<>(
                dtos, resultado.getTotalElementos(), resultado.getPagina() + 1, resultado.getTamPagina());

        return ResponseEntity.ok(ApiResponse.ok(paginaDto, "Listado de preguntas"));
    }

    @GetMapping("/autor/{autorId}")
    @Operation(summary = "Listar preguntas por autor (HU03)", description = "Lista todas las preguntas creadas por un autor específico.")
    public ResponseEntity<ApiResponse<List<PreguntaDTO>>> listarPorAutor(@PathVariable String autorId) {
        List<Question> preguntas = facade.listarPorAutor(autorId);
        List<PreguntaDTO> dtos = preguntas.stream()
                .map(PreguntaDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(dtos, "Preguntas del autor"));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado de una pregunta (HU02)", description = "Permite cambiar el estado de la pregunta respetando las transiciones del ciclo de vida (ej. BORRADOR -> PENDIENTE_REVISION).")
    public ResponseEntity<ApiResponse<PreguntaDTO>> cambiarEstado(
            @PathVariable String id,
            @RequestBody CambioEstadoRequest request) {

        if (request.getNuevoEstado() == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error(
                    List.of("El nuevo estado es requerido"), "Estado inválido"));
        }

        boolean actualizado = facade.cambiarEstado(id, request.getNuevoEstado());
        if (!actualizado) {
            return ResponseEntity.badRequest().body(ApiResponse.error(
                    List.of("Transición no permitida o la pregunta no existe"), "Error al cambiar estado"));
        }

        Question q = facade.obtenerPreguntaPorId(id);
        return ResponseEntity.ok(ApiResponse.ok(PreguntaDTO.fromEntity(q), "Estado actualizado con éxito"));
    }
}
