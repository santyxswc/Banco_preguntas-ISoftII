package co.unicauca.iso2.taller09.userservice.controller;

import co.unicauca.iso2.taller09.common.dto.UserDTO;
import co.unicauca.iso2.taller09.userservice.application.UserDirectoryService;
import co.unicauca.iso2.taller09.userservice.controller.dto.IdentitySyncRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * API interna del contexto de Identidad (RNF #1). Solo la consume el api-gateway dentro de la
 * red de los servicios: el gateway no publica ninguna ruta hacia {@code /internal/**}, así que
 * no es alcanzable desde el navegador ni desde Postman a través del punto de entrada.
 */
@RestController
@RequestMapping("/internal/users")
@Tag(name = "Interno", description = "Sincronización del perfil local con la identidad de Keycloak")
public class InternalUserController {

    private final UserDirectoryService users;

    public InternalUserController(UserDirectoryService users) {
        this.users = users;
    }

    @PostMapping("/sync")
    @Operation(summary = "Sincronizar el perfil local con los datos del JWT",
            description = "Crea el perfil si el usuario entra por primera vez o actualiza nombre, correo y roles. "
                    + "Devuelve el usuario con su identificador numérico, que el gateway propaga en X-User-Id.")
    @ApiResponse(responseCode = "200", description = "Perfil sincronizado")
    @ApiResponse(responseCode = "400", description = "Falta el nombre de usuario")
    public ResponseEntity<UserDTO> synchronize(@RequestBody IdentitySyncRequest request) {
        return ResponseEntity.ok(users.synchronize(request.username(), request.fullName(), request.email(),
                request.roles()).toDTO());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }
}
