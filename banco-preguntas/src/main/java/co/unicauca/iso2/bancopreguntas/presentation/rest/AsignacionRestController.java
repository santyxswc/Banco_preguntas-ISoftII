/**
 * @file AsignacionRestController.java
 * @brief Controlador REST para asignación de revisores (HU04).
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.presentation.rest;

import co.unicauca.iso2.bancopreguntas.domain.Question;
import co.unicauca.iso2.bancopreguntas.domain.facade.BancoPreguntasFacade;
import co.unicauca.iso2.bancopreguntas.presentation.rest.dto.DTOs.ApiResponse;
import co.unicauca.iso2.bancopreguntas.presentation.rest.dto.DTOs.AsignacionRequest;
import co.unicauca.iso2.bancopreguntas.presentation.rest.dto.PreguntaDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/asignaciones")
@CrossOrigin(origins = "*")
@Tag(name = "Asignaciones", description = "Endpoints para la asignación de revisores por parte del administrador (HU04)")
public class AsignacionRestController {

    private final BancoPreguntasFacade facade;

    public AsignacionRestController(BancoPreguntasFacade facade) {
        this.facade = facade;
    }

    @GetMapping("/pendientes")
    @Operation(summary = "Listar preguntas pendientes de asignación", description = "Retorna preguntas en estado PENDIENTE_REVISION listas para asignar revisores.")
    public ResponseEntity<ApiResponse<List<PreguntaDTO>>> listarPendientes() {
        List<Question> preguntas = facade.listarPreguntasPendientesRevision();
        List<PreguntaDTO> dtos = preguntas.stream()
                .map(PreguntaDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(dtos, "Preguntas pendientes de revisión"));
    }

    @PostMapping
    @Operation(summary = "Asignar revisores a una pregunta (HU04)", description = "Asigna entre 1 y 3 revisores a una pregunta en estado PENDIENTE_REVISION y publica el evento para notificación.")
    public ResponseEntity<ApiResponse<Void>> asignarRevisores(@RequestBody AsignacionRequest request) {
        if (request == null || request.getPreguntaId() == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error(
                    List.of("El id de la pregunta es obligatorio"), "Petición inválida"));
        }

        List<String> errores = facade.asignarRevisores(request.getPreguntaId(), request.getRevisorIds());
        if (!errores.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error(errores, "Error al asignar revisores"));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(null, "Revisores asignados exitosamente y notificados por correo"));
    }
}
