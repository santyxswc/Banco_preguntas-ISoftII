/**
 * @file ClientMain.java
 * @brief Punto de entrada de la aplicación.
 * @author Santiago Caicedo
 */
package co.unicauca.iso2.bancopreguntas.app;

import javax.swing.SwingUtilities;

import co.unicauca.iso2.bancopreguntas.access.AsignacionImplRepository;
import co.unicauca.iso2.bancopreguntas.access.QuestionImplRepository;
import co.unicauca.iso2.bancopreguntas.access.UsuarioImplRepository;
import co.unicauca.iso2.bancopreguntas.domain.AsignacionRepository;
import co.unicauca.iso2.bancopreguntas.domain.AsignacionService;
import co.unicauca.iso2.bancopreguntas.domain.QuestionRepository;
import co.unicauca.iso2.bancopreguntas.domain.QuestionService;
import co.unicauca.iso2.bancopreguntas.domain.UsuarioRepository;
import co.unicauca.iso2.bancopreguntas.domain.validacion.ReglaValidacion;
import co.unicauca.iso2.bancopreguntas.domain.validacion.ValidadorEstructuralPregunta;
import co.unicauca.iso2.bancopreguntas.infra.email.EmailNotificacionService;
import co.unicauca.iso2.bancopreguntas.infra.events.PreguntaEventPublisher;
import co.unicauca.iso2.bancopreguntas.presentation.GUILogin;

/**
 * @brief Arma las dependencias de la aplicación y abre el login.
 *
 * Es el único lugar donde se eligen las implementaciones concretas;
 * el resto de clases las reciben por constructor.
 *
 * Por defecto se usan repositorios en memoria. Para trabajar con
 * PostgreSQL basta con cambiar el repositorio de preguntas:
 * @code
 *   DataSource ds = ConexionBD.crearPostgresDesdeEnv();
 *   ConexionBD.migrar(ds);
 *   QuestionRepository questionRepository = new QuestionJdbcRepository(ds);
 * @endcode
 */
public class ClientMain {

    /**
     * @brief Inicia la aplicación.
     * @param args no se usan
     */
    public static void main(String[] args) {

        QuestionRepository questionRepository = new QuestionImplRepository();
        UsuarioRepository usuarioRepository = new UsuarioImplRepository();
        AsignacionRepository asignacionRepository = new AsignacionImplRepository();

        ReglaValidacion validador = new ValidadorEstructuralPregunta();

        QuestionService questionService = new QuestionService(
                questionRepository, usuarioRepository, validador);

        // Sin variables SMTP_* el correo se simula por consola.
        PreguntaEventPublisher eventPublisher = new PreguntaEventPublisher();
        eventPublisher.subscribe(new EmailNotificacionService(usuarioRepository));

        AsignacionService asignacionService = new AsignacionService(
                questionService, asignacionRepository, eventPublisher);

        SwingUtilities.invokeLater(() -> new GUILogin(
                usuarioRepository, questionService, asignacionService)
                .setVisible(true));
    }
}
