/**
 * @file DecoratedRepositoryFactory.java
 * @brief Fábrica concreta que aplica el patrón Decorator sobre los repositorios.
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
 * @brief Fábrica que compone repositorios decorados para auditoría y métricas.
 */
public class DecoratedRepositoryFactory implements RepositoryFactory {

    @Override
    public QuestionRepository createQuestionRepository() {
        return new LoggingQuestionRepositoryDecorator(new QuestionImplRepository());
    }

    @Override
    public UsuarioRepository createUsuarioRepository() {
        return new UsuarioImplRepository();
    }

    @Override
    public AsignacionRepository createAsignacionRepository() {
        return new AsignacionImplRepository();
    }

    @Override
    public RevisionRepository createRevisionRepository() {
        return new RevisionImplRepository();
    }

    @Override
    public CatalogoRepository createCatalogoRepository() {
        return new CatalogoImplRepository();
    }
}
