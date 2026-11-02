package co.unicauca.iso2.taller09.notificationservice.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Patrón GoF <b>Decorator</b> (estructural).
 * <p>
 * Envuelve cualquier {@link EmailSender} y le agrega trazabilidad (canal, destinatario,
 * duración y fallos) sin modificar las clases que realmente envían. Como implementa la misma
 * interfaz, el resto del servicio no sabe si habla con el emisor decorado o con el original.
 */
public class LoggingEmailSenderDecorator implements EmailSender {
    private static final Logger log = LoggerFactory.getLogger(LoggingEmailSenderDecorator.class);

    private final EmailSender wrapped;

    public LoggingEmailSenderDecorator(EmailSender wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void send(EmailMessage message) {
        long start = System.nanoTime();
        try {
            wrapped.send(message);
            log.info("[EMAIL-{}] entregado a {} en {} ms", wrapped.channel(), message.to(), elapsedMs(start));
        } catch (RuntimeException e) {
            log.error("[EMAIL-{}] falló el envío a {} tras {} ms: {}", wrapped.channel(), message.to(), elapsedMs(start), e.getMessage());
            throw e;
        }
    }

    @Override
    public String channel() {
        return wrapped.channel();
    }

    /** El emisor envuelto; útil en pruebas para comprobar qué producto creó la fábrica. */
    public EmailSender wrapped() {
        return wrapped;
    }

    private static long elapsedMs(long start) {
        return (System.nanoTime() - start) / 1_000_000;
    }
}
