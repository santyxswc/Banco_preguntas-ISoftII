package co.unicauca.iso2.taller09.userservice.controller;

import co.unicauca.iso2.taller09.common.dto.LoginRequest;
import co.unicauca.iso2.taller09.common.dto.LoginResponse;
import co.unicauca.iso2.taller09.userservice.domain.User;
import co.unicauca.iso2.taller09.userservice.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Autenticación", description = "Endpoints de autenticación síncrona de usuarios (Punto 1 del taller)")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticación síncrona de usuario", description = "Verifica credenciales de usuario y retorna datos básicos y roles o 401 Unauthorized si son inválidas.")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        if (request == null || request.getUsername() == null || request.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(LoginResponse.failure("Nombre de usuario y contraseña son requeridos"));
        }

        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        if (userOpt.isEmpty() || !userOpt.get().validatePassword(request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(LoginResponse.failure("Credenciales inválidas: usuario o contraseña incorrectos"));
        }

        User user = userOpt.get();
        return ResponseEntity.ok(LoginResponse.success(user.toDTO()));
    }
}
