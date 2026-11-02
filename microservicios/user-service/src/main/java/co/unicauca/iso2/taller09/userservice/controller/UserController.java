package co.unicauca.iso2.taller09.userservice.controller;

import co.unicauca.iso2.taller09.common.constants.IdentityHeaders;
import co.unicauca.iso2.taller09.common.dto.UserDTO;
import co.unicauca.iso2.taller09.userservice.application.UserDirectoryService;
import co.unicauca.iso2.taller09.userservice.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
@Tag(name = "Usuarios", description = "Consulta de usuarios, roles y revisores elegibles")
public class UserController {

    private final UserDirectoryService users;

    public UserController(UserDirectoryService users) {
        this.users = users;
    }

    @GetMapping
    @Operation(summary = "Listar todos los usuarios")
    public ResponseEntity<List<UserDTO>> listAll() {
        return ResponseEntity.ok(toDtos(users.all()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
        return users.byId(id)
                .map(u -> ResponseEntity.ok(u.toDTO()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/role/{role}")
    @Operation(summary = "Listar usuarios por rol (ADMINISTRADOR, AUTOR, REVISOR, DOCENTE, ESTUDIANTE)")
    @ApiResponse(responseCode = "400", description = "Rol desconocido")
    public ResponseEntity<List<UserDTO>> listByRole(@PathVariable String role) {
        return ResponseEntity.ok(toDtos(users.byRole(role)));
    }

    @GetMapping("/reviewers/eligible")
    @Operation(summary = "Revisores elegibles para una asignación",
            description = "Devuelve los usuarios con rol REVISOR, sin el autor de la pregunta ni los revisores ya asignados.")
    public ResponseEntity<List<UserDTO>> eligibleReviewers(@RequestParam(required = false) Long authorId,
                                                           @RequestParam(required = false) List<Long> excluding) {
        return ResponseEntity.ok(toDtos(users.eligibleReviewers(authorId, excluding)));
    }

    @GetMapping("/me")
    @Operation(summary = "Usuario autenticado",
            description = "Usa la cabecera X-User-Id que agrega el api-gateway después de validar el token.")
    @ApiResponse(responseCode = "401", description = "La petición no trae identidad")
    public ResponseEntity<UserDTO> me(@RequestHeader(value = IdentityHeaders.USER_ID, required = false) Long userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return getById(userId);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }

    private List<UserDTO> toDtos(List<User> list) {
        return list.stream().map(User::toDTO).toList();
    }
}
