package co.unicauca.iso2.bancopreguntas.app;

import javax.swing.SwingUtilities;

import co.unicauca.iso2.bancopreguntas.access.QuestionImplRepository;
import co.unicauca.iso2.bancopreguntas.access.UsuarioImplRepository;
import co.unicauca.iso2.bancopreguntas.domain.QuestionRepository;
import co.unicauca.iso2.bancopreguntas.domain.QuestionService;
import co.unicauca.iso2.bancopreguntas.domain.UsuarioRepository;
import co.unicauca.iso2.bancopreguntas.presentation.GUILogin;

/**
 * Composition root de la aplicación.
 *
 * Es el único punto del programa donde se decide qué implementaciones
 * concretas se usan. Ninguna clase de la GUI ni QuestionService
 * construyen sus propias dependencias: todas las reciben por
 * constructor (DIP).
 *
 * Flujo de arranque:
 * <pre>
 *   ClientMain
 *       ↓
 *   GUILogin  (verifica credenciales contra UsuarioRepository)
 *       ↓ (login exitoso)
 *   GUIMain   (según rol: vistas de Autor o Administrador)
 * </pre>
 */
public class ClientMain {

    public static void main(String[] args) {

        // ---- Repositorios (capa de acceso a datos) -----------------
        QuestionRepository questionRepository =
                new QuestionImplRepository();

        UsuarioRepository usuarioRepository =
                new UsuarioImplRepository();

        // ---- Servicio de dominio -----------------------------------
        // QuestionService recibe ambos repositorios para cumplir
        // las operaciones de RF01-RF04.
        QuestionService questionService =
                new QuestionService(questionRepository,
                                    usuarioRepository);

        // ---- Arranque en hilo de despacho de Swing ----------------
        SwingUtilities.invokeLater(() -> {

            // La aplicación arranca siempre con el login.
            // GUILogin abrirá GUIMain tras la autenticación exitosa.
            GUILogin login = new GUILogin(usuarioRepository,
                                          questionService);
            login.setVisible(true);
        });
    }
}
