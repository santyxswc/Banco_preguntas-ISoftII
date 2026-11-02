package co.unicauca.iso2.taller09.notificationservice.email;

import org.springframework.mail.javamail.JavaMailSender;

/**
 * Patrón GoF <b>Factory Method</b> (creacional).
 * <p>
 * El creador abstracto define la operación {@link #sender()}, que necesita un emisor de correo
 * pero no sabe cuál: lo pide al método fábrica {@link #createSender()}, que cada subclase
 * implementa. Así el servicio pasa de correo simulado a SMTP real cambiando solo la propiedad
 * {@code notifications.email.mode}, sin tocar el código que notifica.
 */
public abstract class EmailSenderFactory {

    /** Método fábrica: cada subclase decide qué emisor concreto crear. */
    protected abstract EmailSender createSender();

    /** Operación del creador: todo emisor sale decorado con trazabilidad. */
    public final EmailSender sender() {
        return new LoggingEmailSenderDecorator(createSender());
    }

    /** Elige la fábrica concreta según el modo configurado ("console" por defecto o "smtp"). */
    public static EmailSenderFactory forMode(String mode, JavaMailSender mailSender, String from) {
        if ("smtp".equalsIgnoreCase(mode)) {
            if (mailSender == null) {
                throw new IllegalStateException("notifications.email.mode=smtp requiere configurar spring.mail.host");
            }
            return new SmtpEmailSenderFactory(mailSender, from);
        }
        return new ConsoleEmailSenderFactory();
    }

    /** Fábrica concreta del correo simulado en consola. */
    public static class ConsoleEmailSenderFactory extends EmailSenderFactory {
        @Override
        protected EmailSender createSender() {
            return new ConsoleEmailSender();
        }
    }

    /** Fábrica concreta del correo real por SMTP. */
    public static class SmtpEmailSenderFactory extends EmailSenderFactory {
        private final JavaMailSender mailSender;
        private final String from;

        public SmtpEmailSenderFactory(JavaMailSender mailSender, String from) {
            this.mailSender = mailSender;
            this.from = from;
        }

        @Override
        protected EmailSender createSender() {
            return new SmtpEmailSender(mailSender, from);
        }
    }
}
