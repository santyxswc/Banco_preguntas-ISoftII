/**
 * @file AsignacionController.java
 * @brief Controlador MVC para asignar revisores.
 * @author Santiago Caicedo
 */
package co.unicauca.iso2.bancopreguntas.presentation;

import co.unicauca.iso2.bancopreguntas.domain.AsignacionService;

import java.util.List;

/**
 * @brief Recibe la selección de GUIAsignarRevisores y la delega en
 *        AsignacionService.
 */
public class AsignacionController {

    private final AsignacionService asignacionService;

    /** @param asignacionService servicio de asignación */
    public AsignacionController(AsignacionService asignacionService) {
        this.asignacionService = asignacionService;
    }

    /**
     * @param preguntaId id de la pregunta
     * @param revisorIds ids de los revisores marcados
     * @return true si la asignación se realizó
     */
    public boolean asignarRevisores(String preguntaId, List<String> revisorIds) {
        return asignacionService.asignarRevisores(preguntaId, revisorIds);
    }
}
