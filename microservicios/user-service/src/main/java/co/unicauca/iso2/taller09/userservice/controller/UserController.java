package co.unicauca.iso2.taller09.userservice.controller;

import co.unicauca.iso2.taller09.common.dto.UserDTO;
import co.unicauca.iso2.taller09.userservice.domain.User;
import co.unicauca.iso2.taller09.userservice.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
@Tag(name = "Usuarios", description = "Endpoints de consulta de usuarios y verificación de roles")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    @Operation(summary = "Listar todos los usuarios")
    public ResponseEntity<List<UserDTO>> listAll() {
        List<UserDTO> dtos = userRepository.findAll().stream()
                .map(User::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    public ResponseEntity<UserDTO> getById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(u -> ResponseEntity.ok(u.toDTO()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/role/{role}")
    @Operation(summary = "Listar usuarios por rol")
    public ResponseEntity<List<UserDTO>> listByRole(@PathVariable String role) {
        List<UserDTO> dtos = userRepository.findAll().stream()
                .filter(u -> u.hasRole(role.toUpperCase()))
                .map(User::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
