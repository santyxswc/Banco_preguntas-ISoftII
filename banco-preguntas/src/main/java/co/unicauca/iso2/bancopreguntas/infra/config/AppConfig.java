/**
 * @file AppConfig.java
 * @brief Configuración del contenedor IoC de Spring para el dominio y persistencia.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.infra.config;

import co.unicauca.iso2.bancopreguntas.access.AsignacionImplRepository;
import co.unicauca.iso2.bancopreguntas.access.CatalogoImplRepository;
import co.unicauca.iso2.bancopreguntas.access.QuestionImplRepository;
import co.unicauca.iso2.bancopreguntas.access.QuestionJdbcRepository;
import co.unicauca.iso2.bancopreguntas.access.RevisionImplRepository;
import co.unicauca.iso2.bancopreguntas.access.UsuarioImplRepository;
import co.unicauca.iso2.bancopreguntas.access.db.ConexionBD;
import co.unicauca.iso2.bancopreguntas.access.decorator.LoggingQuestionRepositoryDecorator;
import co.unicauca.iso2.bancopreguntas.domain.AsignacionRepository;
import co.unicauca.iso2.bancopreguntas.domain.AsignacionService;
import co.unicauca.iso2.bancopreguntas.domain.CatalogoRepository;
import co.unicauca.iso2.bancopreguntas.domain.QuestionRepository;
import co.unicauca.iso2.bancopreguntas.domain.QuestionService;
import co.unicauca.iso2.bancopreguntas.domain.RevisionRepository;
import co.unicauca.iso2.bancopreguntas.domain.RevisionService;
import co.unicauca.iso2.bancopreguntas.domain.UsuarioRepository;
import co.unicauca.iso2.bancopreguntas.domain.facade.BancoPreguntasFacade;
import co.unicauca.iso2.bancopreguntas.domain.validacion.ReglaValidacion;
import co.unicauca.iso2.bancopreguntas.domain.validacion.ValidadorEstructuralPregunta;
import co.unicauca.iso2.bancopreguntas.infra.email.EmailNotificacionService;
import co.unicauca.iso2.bancopreguntas.infra.email.EmailRevisionNotificacionService;
import co.unicauca.iso2.bancopreguntas.infra.events.EventBus;
import co.unicauca.iso2.bancopreguntas.infra.events.PreguntaEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class AppConfig {

    @Bean
    public UsuarioRepository usuarioRepository() {
        return new UsuarioImplRepository();
    }

    @Bean
    public CatalogoRepository catalogoRepository() {
        return new CatalogoImplRepository();
    }

    @Bean
    public AsignacionRepository asignacionRepository() {
        return new AsignacionImplRepository();
    }

    @Bean
    public RevisionRepository revisionRepository() {
        return new RevisionImplRepository();
    }

    @Bean
    public QuestionRepository questionRepository() {
        QuestionRepository baseRepo;
        try {
            DataSource ds = ConexionBD.crearPostgresDesdeEnv();
            ConexionBD.migrarConDatosDeEjemplo(ds);
            System.out.println("[SPRING-BD] Conectado a PostgreSQL con Flyway.");
            baseRepo = new QuestionJdbcRepository(ds);
        } catch (RuntimeException e) {
            System.out.println("[SPRING-BD] Usando repositorio en memoria (" + e.getMessage() + ")");
            baseRepo = new QuestionImplRepository();
        }
        // Aplicamos el patrón GoF Decorator para logging y auditoría
        return new LoggingQuestionRepositoryDecorator(baseRepo);
    }

    @Bean
    public ReglaValidacion reglaValidacion() {
        return new ValidadorEstructuralPregunta();
    }

    @Bean
    public EventBus eventBus(UsuarioRepository usuarioRepository, QuestionRepository questionRepository) {
        EventBus bus = EventBus.getInstance();
        bus.subscribe(new EmailNotificacionService(usuarioRepository));
        bus.subscribeRevision(new EmailRevisionNotificacionService(usuarioRepository, questionRepository));
        return bus;
    }

    @Bean
    public PreguntaEventPublisher preguntaEventPublisher(EventBus eventBus) {
        return eventBus.getPublisher();
    }

    @Bean
    public QuestionService questionService(QuestionRepository questionRepository,
                                           UsuarioRepository usuarioRepository,
                                           ReglaValidacion validador) {
        return new QuestionService(questionRepository, usuarioRepository, validador);
    }

    @Bean
    public AsignacionService asignacionService(QuestionService questionService,
                                             AsignacionRepository asignacionRepository,
                                             PreguntaEventPublisher publisher) {
        return new AsignacionService(questionService, asignacionRepository, publisher);
    }

    @Bean
    public RevisionService revisionService(QuestionService questionService,
                                           RevisionRepository revisionRepository,
                                           AsignacionRepository asignacionRepository,
                                           PreguntaEventPublisher publisher) {
        return new RevisionService(questionService, revisionRepository, asignacionRepository, publisher);
    }

    @Bean
    public BancoPreguntasFacade bancoPreguntasFacade(QuestionService questionService,
                                                     AsignacionService asignacionService,
                                                     RevisionService revisionService,
                                                     UsuarioRepository usuarioRepository) {
        return new BancoPreguntasFacade(questionService, asignacionService, revisionService, usuarioRepository);
    }
}
