/**
 * @file CatalogoRepository.java
 * @brief Contrato de acceso al catálogo académico.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.domain;

import java.util.List;

/**
 * @brief Consulta del catálogo Competencia → Tema → Subtema.
 */
public interface CatalogoRepository {

    /** @return competencias registradas */
    List<Competencia> listCompetencias();

    /**
     * @param competenciaId id de la competencia
     * @return temas de esa competencia
     */
    List<Tema> listTemasPorCompetencia(String competenciaId);

    /**
     * @param temaId id del tema
     * @return subtemas de ese tema
     */
    List<Subtema> listSubtemasPorTema(String temaId);
}
