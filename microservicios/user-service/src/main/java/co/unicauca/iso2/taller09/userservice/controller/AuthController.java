package co.unicauca.iso2.taller09.userservice.controller;

import co.unicauca.iso2.taller09.common.dto.LoginRequest;
import co.unicauca.iso2.taller09.common.dto.LoginResponse;
import co.unicauca.iso2.taller09.common.security.AuthTokenCodec;
import co.unicauca.iso2.taller09.userservice.application.UserDirectoryService;
import co.unicauca.iso2.taller09.userservice.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Autenticación", description = "Autenticación síncrona de usuarios y emisión del token de identidad")
public class AuthController {

    private final UserDirectoryService users;
    private final AuthTokenCodec tokens;

    public AuthController(UserDirectoryService users, AuthTokenCodec tokens) {
        this.users = users;
        this.tokens = tokens;
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticación síncrona de usuario",
            description = "Verifica las credenciales y devuelve los datos del usuario, sus roles y un token firmado que el api-gateway usa para propagar la identidad.")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        if (request == null || request.getUsername() == null || request.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(LoginResponse.failure("Nombre de usuario y contraseña son requeridos"));
        }
        Optional<User> user = users.authenticate(request.getUsername(), request.getPassword());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(LoginResponse.failure("Credenciales inválidas: usuario o contraseña incorrectos"));
        }
        User authenticated = user.get();
        String token = tokens.issue(authenticated.getId(), authenticated.getUsername(), authenticated.roleNames());
        return ResponseEntity.ok(LoginResponse.success(authenticated.toDTO(), token));
    }
}
