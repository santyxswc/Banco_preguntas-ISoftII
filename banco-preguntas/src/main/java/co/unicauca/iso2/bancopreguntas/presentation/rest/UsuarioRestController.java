/**
 * @file UsuarioRestController.java
 * @brief Controlador REST para consulta de usuarios y roles.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.presentation.rest;

import co.unicauca.iso2.bancopreguntas.domain.Rol;
import co.unicauca.iso2.bancopreguntas.domain.Usuario;
import co.unicauca.iso2.bancopreguntas.domain.UsuarioRepository;
import co.unicauca.iso2.bancopreguntas.presentation.rest.dto.DTOs.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/usuarios")
@CrossOrigin(origins = "*")
@Tag(name = "Usuarios", description = "Endpoints para la gestión y consulta de usuarios (Autores, Revisores, Administradores)")
public class UsuarioRestController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioRestController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    @Operation(summary = "Listar todos los usuarios")
    public ResponseEntity<ApiResponse<List<Usuario>>> listarTodos() {
        return ResponseEntity.ok(ApiResponse.ok(usuarioRepository.list(), "Listado de usuarios"));
    }

    @GetMapping("/revisores")
    @Operation(summary = "Listar usuarios con rol REVISOR")
    public ResponseEntity<ApiResponse<List<Usuario>>> listarRevisores() {
        List<Usuario> revisores = usuarioRepository.list().stream()
                .filter(u -> u.getRol() == Rol.REVISOR)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(revisores, "Listado de revisores"));
    }

    @GetMapping("/autores")
    @Operation(summary = "Listar usuarios con rol AUTOR")
    public ResponseEntity<ApiResponse<List<Usuario>>> listarAutores() {
        List<Usuario> autores = usuarioRepository.list().stream()
                .filter(u -> u.getRol() == Rol.AUTOR)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(autores, "Listado de autores"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    public ResponseEntity<ApiResponse<Usuario>> obtenerPorId(@PathVariable String id) {
        Usuario u = usuarioRepository.findById(id);
        if (u == null) {
            return ResponseEntity.status(404).body(ApiResponse.error(List.of("Usuario no encontrado"), "No existe"));
        }
        return ResponseEntity.ok(ApiResponse.ok(u, "Usuario encontrado"));
    }
}
