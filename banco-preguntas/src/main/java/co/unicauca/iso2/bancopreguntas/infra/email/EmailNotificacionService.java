/**
 * @file EmailNotificacionService.java
 * @brief Notificación por correo a los revisores asignados.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.infra.email;

import co.unicauca.iso2.bancopreguntas.domain.Usuario;
import co.unicauca.iso2.bancopreguntas.domain.UsuarioRepository;
import co.unicauca.iso2.bancopreguntas.infra.events.EventListener;
import co.unicauca.iso2.bancopreguntas.infra.events.RevisorAsignadoEvent;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

/**
 * @brief Envía un correo a cada revisor cuando se publica un
 *        RevisorAsignadoEvent.
 *
 * Usa Jakarta Mail con la configuración de las variables SMTP_HOST,
 * SMTP_PORT, SMTP_USER, SMTP_PASSWORD y SMTP_FROM. Si no hay SMTP_HOST
 * el envío se simula imprimiendo en consola. Un fallo al enviar se
 * registra pero no deshace la asignación.
 */
public class EmailNotificacionService implements EventListener<RevisorAsignadoEvent> {

    private final UsuarioRepository usuarioRepository;
    private final String host;
    private final int puerto;
    private final String usuarioSmtp;
    private final String passwordSmtp;
    private final String remitente;
    private final boolean habilitado;

    /**
     * @brief Crea el servicio con la configuración SMTP del entorno.
     * @param usuarioRepository repositorio para obtener los correos
     */
    public EmailNotificacionService(UsuarioRepository usuarioRepository) {
        this(usuarioRepository,
                System.getenv("SMTP_HOST"),
                parsePuerto(System.getenv("SMTP_PORT")),
                System.getenv("SMTP_USER"),
                System.getenv("SMTP_PASSWORD"),
                System.getenv("SMTP_FROM"));
    }

    public EmailNotificacionService(UsuarioRepository usuarioRepository,
                                     String host, int puerto,
                                     String usuarioSmtp, String passwordSmtp,
                                     String remitente) {
        this.usuarioRepository = usuarioRepository;
        this.host = host;
        this.puerto = puerto;
        this.usuarioSmtp = usuarioSmtp;
        this.passwordSmtp = passwordSmtp;
        this.remitente = (remitente != null && !remitente.isBlank())
                ? remitente : "banco-preguntas@unicauca.edu.co";
        this.habilitado = host != null && !host.isBlank();
    }

    private static int parsePuerto(String valor) {
        try {
            return (valor != null && !valor.isBlank())
                    ? Integer.parseInt(valor) : 587;
        } catch (NumberFormatException e) {
            return 587;
        }
    }

    @Override
    public void onEvent(RevisorAsignadoEvent evento) {
        for (String revisorId : evento.getRevisorIds()) {
            enviarNotificacion(evento.getPreguntaId(), revisorId);
        }
    }

    private void enviarNotificacion(String preguntaId, String revisorId) {

        String destino = resolverEmail(revisorId);

        if (!habilitado) {
            System.out.println("[CORREO-SIMULADO] Notificación para "
                    + destino + " sobre la pregunta " + preguntaId);
            return;
        }

        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", String.valueOf(puerto));

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(usuarioSmtp, passwordSmtp);
                }
            });

            MimeMessage mensaje = new MimeMessage(session);
            mensaje.setFrom(new InternetAddress(remitente));
            mensaje.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(destino));
            mensaje.setSubject("Nueva pregunta asignada para revisión");
            mensaje.setText("Se te ha asignado la pregunta " + preguntaId
                    + " para revisión en el Banco de Preguntas Saber Pro.");

            Transport.send(mensaje);

            System.out.println("[CORREO] Notificación enviada a " + destino
                    + " (pregunta " + preguntaId + ")");

        } catch (MessagingException e) {
            System.err.println("[CORREO-ERROR] No se pudo notificar a "
                    + destino + " (pregunta " + preguntaId + "): "
                    + e.getMessage());
        }
    }

    private String resolverEmail(String revisorId) {
        if (usuarioRepository == null) {
            return revisorId;
        }
        Usuario usuario = usuarioRepository.findById(revisorId);
        return (usuario != null && usuario.getEmail() != null)
                ? usuario.getEmail() : revisorId;
    }
}
