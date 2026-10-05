/**
 * @file RepositoryFactory.java
 * @brief Patrón GoF Creacional: Factory Method para creación de repositorios.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.access.factory;

import co.unicauca.iso2.bancopreguntas.access.AsignacionImplRepository;
import co.unicauca.iso2.bancopreguntas.access.CatalogoImplRepository;
import co.unicauca.iso2.bancopreguntas.access.QuestionImplRepository;
import co.unicauca.iso2.bancopreguntas.access.RevisionImplRepository;
import co.unicauca.iso2.bancopreguntas.access.UsuarioImplRepository;
import co.unicauca.iso2.bancopreguntas.access.decorator.LoggingQuestionRepositoryDecorator;
import co.unicauca.iso2.bancopreguntas.domain.AsignacionRepository;
import co.unicauca.iso2.bancopreguntas.domain.CatalogoRepository;
import co.unicauca.iso2.bancopreguntas.domain.QuestionRepository;
import co.unicauca.iso2.bancopreguntas.domain.RevisionRepository;
import co.unicauca.iso2.bancopreguntas.domain.UsuarioRepository;

/**
 * @brief Fábrica abstracta / Factory Method para desacoplar la creación de repositorios.
 *
 * Permite cambiar la estrategia de persistencia (en memoria, JDBC, decorada con logging)
 * sin modificar la lógica de negocio en los servicios.
 */
public interface RepositoryFactory {

    QuestionRepository createQuestionRepository();
    UsuarioRepository createUsuarioRepository();
    AsignacionRepository createAsignacionRepository();
    RevisionRepository createRevisionRepository();
    CatalogoRepository createCatalogoRepository();

    /**
     * @brief Crea una fábrica según el tipo solicitado.
     * @param tipo "IN_MEMORY", "DECORATED"
     * @return instancia de RepositoryFactory
     */
    static RepositoryFactory getFactory(String tipo) {
        if ("DECORATED".equalsIgnoreCase(tipo)) {
            return new DecoratedRepositoryFactory();
        }
        return new InMemoryRepositoryFactory();
    }
}
