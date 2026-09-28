/**
 * @file ClientMain.java
 * @brief Punto de entrada de la aplicación.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.app;

import javax.sql.DataSource;
import javax.swing.SwingUtilities;

import co.unicauca.iso2.bancopreguntas.access.AsignacionImplRepository;
import co.unicauca.iso2.bancopreguntas.access.QuestionImplRepository;
import co.unicauca.iso2.bancopreguntas.access.QuestionJdbcRepository;
import co.unicauca.iso2.bancopreguntas.access.UsuarioImplRepository;
import co.unicauca.iso2.bancopreguntas.access.db.ConexionBD;
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
 * Las preguntas se guardan en PostgreSQL (variables DB_*, ver
 * ConexionBD). Si no hay conexión se usa el repositorio en memoria
 * para que la aplicación siga funcionando. Usuarios y asignaciones
 * siguen en memoria en esta iteración.
 */
public class ClientMain {

    /**
     * @brief Inicia la aplicación.
     * @param args no se usan
     */
    public static void main(String[] args) {

        QuestionRepository questionRepository = crearRepositorioPreguntas();
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

    /**
     * @brief Repositorio JDBC sobre PostgreSQL; si la base no responde,
     *        repositorio en memoria.
     */
    private static QuestionRepository crearRepositorioPreguntas() {
        try {
            DataSource ds = ConexionBD.crearPostgresDesdeEnv();
            ConexionBD.migrarConDatosDeEjemplo(ds);
            System.out.println("[BD] Conectado a PostgreSQL.");
            return new QuestionJdbcRepository(ds);
        } catch (RuntimeException e) {
            System.err.println("[BD] No se pudo conectar a PostgreSQL ("
                    + e.getMessage() + "). Se usan datos en memoria.");
            return new QuestionImplRepository();
        }
    }
}
