package co.unicauca.iso2.taller09.notificationservice;

import co.unicauca.iso2.taller09.common.enums.NotificationStatus;
import co.unicauca.iso2.taller09.common.enums.NotificationType;
import co.unicauca.iso2.taller09.notificationservice.application.NotificationDispatcher;
import co.unicauca.iso2.taller09.notificationservice.domain.Notification;
import co.unicauca.iso2.taller09.notificationservice.email.ConsoleEmailSender;
import co.unicauca.iso2.taller09.notificationservice.email.EmailDeliveryException;
import co.unicauca.iso2.taller09.notificationservice.email.EmailMessage;
import co.unicauca.iso2.taller09.notificationservice.email.EmailSender;
import co.unicauca.iso2.taller09.notificationservice.email.EmailSenderFactory;
import co.unicauca.iso2.taller09.notificationservice.email.LoggingEmailSenderDecorator;
import co.unicauca.iso2.taller09.notificationservice.email.SmtpEmailSender;
import co.unicauca.iso2.taller09.notificationservice.repository.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias de Factory Method, Decorator y del despachador de correos. */
class EmailNotificationTest {

    /** Emisor de prueba que guarda los correos o falla a demanda. */
    static class RecordingSender implements EmailSender {
        final List<EmailMessage> sent = new ArrayList<>();
        boolean fail;

        @Override
        public void send(EmailMessage message) {
            if (fail) throw new EmailDeliveryException("SMTP caído", null);
            sent.add(message);
        }

        @Override
        public String channel() {
            return "prueba";
        }
    }

    @Test
    @DisplayName("Factory Method: el modo por defecto crea el emisor de consola, decorado")
    void fabricaConsola() {
        EmailSender sender = EmailSenderFactory.forMode("console", null, "x@y.co").sender();
        assertInstanceOf(LoggingEmailSenderDecorator.class, sender);
        assertInstanceOf(ConsoleEmailSender.class, ((LoggingEmailSenderDecorator) sender).wrapped());
        assertEquals("consola", sender.channel());
    }

    @Test
    @DisplayName("Factory Method: el modo smtp crea el emisor SMTP")
    void fabricaSmtp() {
        EmailSender sender = EmailSenderFactory.forMode("SMTP", new JavaMailSenderImpl(), "x@y.co").sender();
        assertInstanceOf(SmtpEmailSender.class, ((LoggingEmailSenderDecorator) sender).wrapped());
    }

    @Test
    @DisplayName("Factory Method: smtp sin servidor configurado falla al arrancar, no al primer correo")
    void smtpSinServidor() {
        assertThrows(IllegalStateException.class, () -> EmailSenderFactory.forMode("smtp", null, "x@y.co"));
    }

    @Test
    @DisplayName("Decorator: delega el envío y propaga los fallos")
    void decorador() {
        RecordingSender real = new RecordingSender();
        EmailSender decorated = new LoggingEmailSenderDecorator(real);

        decorated.send(new EmailMessage("a@b.co", "Asunto", "Cuerpo"));
        assertEquals(1, real.sent.size());
        assertEquals("prueba", decorated.channel());

        real.fail = true;
        assertThrows(EmailDeliveryException.class, () -> decorated.send(new EmailMessage("a@b.co", "s", "b")));
    }

    @Test
    @DisplayName("El despachador envía al correo del usuario y marca la notificación como SENT")
    void despachoExitoso() {
        NotificationRepository repo = new NotificationRepository();
        RecordingSender sender = new RecordingSender();
        NotificationDispatcher dispatcher = new NotificationDispatcher(repo, sender, id -> "revisor" + id + "@unicauca.edu.co");

        Notification n = dispatcher.notify(4L, NotificationType.REVIEWER_ASSIGNED, "Asignada", "Evalúe la pregunta");

        assertEquals(NotificationStatus.SENT, n.getStatus());
        assertEquals("revisor4@unicauca.edu.co", n.getRecipientEmail());
        assertEquals("revisor4@unicauca.edu.co", sender.sent.get(0).to());
        assertEquals("revisor4@unicauca.edu.co", n.toDTO().getRecipientEmail());
    }

    @Test
    @DisplayName("Si el correo falla la notificación queda en FAILED y no se pierde")
    void despachoFallido() {
        NotificationRepository repo = new NotificationRepository();
        RecordingSender sender = new RecordingSender();
        sender.fail = true;
        NotificationDispatcher dispatcher = new NotificationDispatcher(repo, sender, id -> "x@y.co");

        Notification n = dispatcher.notify(2L, NotificationType.QUESTION_APPROVED, "Aprobada", "Felicitaciones");

        assertEquals(NotificationStatus.FAILED, n.getStatus());
        assertEquals(1, repo.findByRecipientId(2L).size());
    }
}
