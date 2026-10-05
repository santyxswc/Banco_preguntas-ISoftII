/**
 * @file RepositoryFactoryTest.java
 * @brief Pruebas unitarias para el patrón GoF Factory Method (RepositoryFactory).
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.access.factory;

import co.unicauca.iso2.bancopreguntas.access.decorator.LoggingQuestionRepositoryDecorator;
import co.unicauca.iso2.bancopreguntas.domain.QuestionRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RepositoryFactoryTest {

    @Test
    void inMemoryFactoryCreaInstanciasCorrectas() {
        RepositoryFactory factory = RepositoryFactory.getFactory("IN_MEMORY");
        assertInstanceOf(InMemoryRepositoryFactory.class, factory);

        assertNotNull(factory.createQuestionRepository());
        assertNotNull(factory.createUsuarioRepository());
        assertNotNull(factory.createAsignacionRepository());
        assertNotNull(factory.createRevisionRepository());
        assertNotNull(factory.createCatalogoRepository());
    }

    @Test
    void decoratedFactoryCreaQuestionRepositoryConDecorador() {
        RepositoryFactory factory = RepositoryFactory.getFactory("DECORATED");
        assertInstanceOf(DecoratedRepositoryFactory.class, factory);

        QuestionRepository repo = factory.createQuestionRepository();
        assertNotNull(repo);
        assertInstanceOf(LoggingQuestionRepositoryDecorator.class, repo);
    }
}
