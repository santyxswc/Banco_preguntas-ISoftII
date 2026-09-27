/**
 * @file AsignacionImplRepository.java
 * @brief Repositorio en memoria de asignaciones.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.access;

import co.unicauca.iso2.bancopreguntas.domain.Asignacion;
import co.unicauca.iso2.bancopreguntas.domain.AsignacionRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @brief Implementación en memoria de AsignacionRepository.
 */
public class AsignacionImplRepository implements AsignacionRepository {

    private final Map<String, Asignacion> asignaciones = new LinkedHashMap<>();

    @Override
    public Asignacion save(Asignacion asignacion) {

        if (asignacion == null) {
            return null;
        }

        if (asignacion.getId() == null || asignacion.getId().isBlank()) {
            asignacion.setId("ASIG-" + UUID.randomUUID()
                    .toString().substring(0, 8).toUpperCase());
        }

        asignaciones.put(asignacion.getId(), asignacion);
        return asignacion;
    }

    @Override
    public List<Asignacion> findByPreguntaId(String preguntaId) {
        List<Asignacion> resultado = new ArrayList<>();
        for (Asignacion a : asignaciones.values()) {
            if (preguntaId != null && preguntaId.equals(a.getPreguntaId())) {
                resultado.add(a);
            }
        }
        return resultado;
    }

    @Override
    public List<Asignacion> list() {
        return new ArrayList<>(asignaciones.values());
    }
}
