/**
 * @file CatalogoRestController.java
 * @brief Controlador REST para consulta de catálogos (Competencias, Temas, Subtemas, Dificultad, Estados).
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.presentation.rest;

import co.unicauca.iso2.bancopreguntas.domain.CatalogoRepository;
import co.unicauca.iso2.bancopreguntas.domain.Competencia;
import co.unicauca.iso2.bancopreguntas.domain.EstadoPregunta;
import co.unicauca.iso2.bancopreguntas.domain.NivelDificultad;
import co.unicauca.iso2.bancopreguntas.domain.Rol;
import co.unicauca.iso2.bancopreguntas.domain.Subtema;
import co.unicauca.iso2.bancopreguntas.domain.Tema;
import co.unicauca.iso2.bancopreguntas.presentation.rest.dto.DTOs.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/catalogos")
@CrossOrigin(origins = "*")
@Tag(name = "Catálogos", description = "Endpoints para consultar taxonomía Saber Pro y enums del sistema")
public class CatalogoRestController {

    private final CatalogoRepository catalogoRepository;

    public CatalogoRestController(CatalogoRepository catalogoRepository) {
        this.catalogoRepository = catalogoRepository;
    }

    @GetMapping("/competencias")
    @Operation(summary = "Listar competencias Saber Pro")
    public ResponseEntity<ApiResponse<List<Competencia>>> listarCompetencias() {
        return ResponseEntity.ok(ApiResponse.ok(catalogoRepository.listCompetencias(), "Competencias"));
    }

    @GetMapping("/temas/{competenciaId}")
    @Operation(summary = "Listar temas por competencia")
    public ResponseEntity<ApiResponse<List<Tema>>> listarTemas(@PathVariable String competenciaId) {
        return ResponseEntity.ok(ApiResponse.ok(catalogoRepository.listTemasPorCompetencia(competenciaId), "Temas"));
    }

    @GetMapping("/subtemas/{temaId}")
    @Operation(summary = "Listar subtemas por tema")
    public ResponseEntity<ApiResponse<List<Subtema>>> listarSubtemas(@PathVariable String temaId) {
        return ResponseEntity.ok(ApiResponse.ok(catalogoRepository.listSubtemasPorTema(temaId), "Subtemas"));
    }

    @GetMapping("/dificultades")
    @Operation(summary = "Listar niveles de dificultad")
    public ResponseEntity<ApiResponse<List<NivelDificultad>>> listarDificultades() {
        return ResponseEntity.ok(ApiResponse.ok(Arrays.asList(NivelDificultad.values()), "Niveles de dificultad"));
    }

    @GetMapping("/estados")
    @Operation(summary = "Listar estados del ciclo de vida con colores hexadecimales")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> listarEstados() {
        List<Map<String, Object>> estados = Arrays.stream(EstadoPregunta.values())
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("estado", e.name());
                    map.put("etiqueta", e.getEtiqueta());
                    map.put("colorHex", e.colorHex());
                    map.put("permiteEdicion", e.permiteEdicion());
                    return map;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(estados, "Estados del ciclo de vida"));
    }

    @GetMapping("/roles")
    @Operation(summary = "Listar roles del sistema")
    public ResponseEntity<ApiResponse<List<Rol>>> listarRoles() {
        return ResponseEntity.ok(ApiResponse.ok(Arrays.asList(Rol.values()), "Roles de usuario"));
    }
}
