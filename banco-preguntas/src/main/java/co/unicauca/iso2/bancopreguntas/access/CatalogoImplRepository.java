/**
 * @file CatalogoImplRepository.java
 * @brief Repositorio en memoria del catálogo académico.
 * @author Santiago Caicedo
 */
package co.unicauca.iso2.bancopreguntas.access;

import co.unicauca.iso2.bancopreguntas.domain.CatalogoRepository;
import co.unicauca.iso2.bancopreguntas.domain.Competencia;
import co.unicauca.iso2.bancopreguntas.domain.Subtema;
import co.unicauca.iso2.bancopreguntas.domain.Tema;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @brief Implementación en memoria de CatalogoRepository.
 *
 * Se carga con las competencias, temas y subtemas de los datos de
 * ejemplo.
 */
public class CatalogoImplRepository implements CatalogoRepository {

    private final Map<String, Competencia> competencias = new LinkedHashMap<>();
    private final Map<String, Tema> temas = new LinkedHashMap<>();
    private final Map<String, Subtema> subtemas = new LinkedHashMap<>();

    public CatalogoImplRepository() {
        cargarCatalogoDeEjemplo();
    }

    @Override
    public List<Competencia> listCompetencias() {
        return new ArrayList<>(competencias.values());
    }

    @Override
    public List<Tema> listTemasPorCompetencia(String competenciaId) {
        List<Tema> resultado = new ArrayList<>();
        for (Tema t : temas.values()) {
            if (competenciaId == null || competenciaId.equals(t.getCompetenciaId())) {
                resultado.add(t);
            }
        }
        return resultado;
    }

    @Override
    public List<Subtema> listSubtemasPorTema(String temaId) {
        List<Subtema> resultado = new ArrayList<>();
        for (Subtema s : subtemas.values()) {
            if (temaId == null || temaId.equals(s.getTemaId())) {
                resultado.add(s);
            }
        }
        return resultado;
    }

    /** @brief Carga el catálogo inicial de ejemplo. */
    private void cargarCatalogoDeEjemplo() {

        agregarCompetencia("C-01", "Lectura crítica");
        agregarTema("T-01", "Comprensión textual", "C-01");
        agregarSubtema("S-01", "Tipos de texto", "T-01");
        agregarSubtema("S-02", "Inferencia", "T-01");

        agregarCompetencia("C-02", "Razonamiento cuantitativo");
        agregarTema("T-02", "Proporciones y porcentajes", "C-02");
        agregarSubtema("S-03", "Porcentajes", "T-02");

        agregarCompetencia("C-03", "Competencias ciudadanas");
        agregarTema("T-03", "Participación y responsabilidad democrática", "C-03");
        agregarSubtema("S-04", "Mecanismos de participación", "T-03");

        agregarCompetencia("C-04", "Comunicación escrita");
        agregarTema("T-04", "Cohesión textual", "C-04");
        agregarSubtema("S-05", "Conectores lógicos", "T-04");

        agregarCompetencia("C-05", "Inglés");
        agregarTema("T-05", "Gramática", "C-05");
        agregarSubtema("S-06", "Presente simple", "T-05");
    }

    private void agregarCompetencia(String id, String nombre) {
        competencias.put(id, new Competencia(id, nombre));
    }

    private void agregarTema(String id, String nombre, String competenciaId) {
        temas.put(id, new Tema(id, nombre, competenciaId));
    }

    private void agregarSubtema(String id, String nombre, String temaId) {
        subtemas.put(id, new Subtema(id, nombre, temaId));
    }
}
