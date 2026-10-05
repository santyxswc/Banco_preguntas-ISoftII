/**
 * @file RevisionImplRepository.java
 * @brief Repositorio en memoria de revisiones.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.access;

import co.unicauca.iso2.bancopreguntas.domain.Revision;
import co.unicauca.iso2.bancopreguntas.domain.RevisionRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @brief Implementación en memoria del repositorio de revisiones (HU05).
 */
public class RevisionImplRepository implements RevisionRepository {

    private final Map<String, Revision> revisiones = new LinkedHashMap<>();

    @Override
    public Revision save(Revision revision) {
        if (revision == null) {
            return null;
        }

        if (revision.getId() == null || revision.getId().isBlank()) {
            revision.setId("REV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        revisiones.put(revision.getId(), revision);
        return revision;
    }

    @Override
    public List<Revision> findByPreguntaId(String preguntaId) {
        List<Revision> resultado = new ArrayList<>();
        for (Revision r : revisiones.values()) {
            if (preguntaId != null && preguntaId.equals(r.getPreguntaId())) {
                resultado.add(r);
            }
        }
        return resultado;
    }

    @Override
    public List<Revision> findByRevisorId(String revisorId) {
        List<Revision> resultado = new ArrayList<>();
        for (Revision r : revisiones.values()) {
            if (revisorId != null && revisorId.equals(r.getRevisorId())) {
                resultado.add(r);
            }
        }
        return resultado;
    }

    @Override
    public List<Revision> list() {
        return new ArrayList<>(revisiones.values());
    }
}
