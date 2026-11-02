package co.unicauca.iso2.taller09.notificationservice.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Envío simulado: escribe el correo completo en la consola. Es el canal por defecto para
 * desarrollo y para la demostración, porque no requiere un servidor SMTP.
 */
public class ConsoleEmailSender implements EmailSender {
    private static final Logger log = LoggerFactory.getLogger(ConsoleEmailSender.class);

    @Override
    public void send(EmailMessage message) {
        log.info("""

                ==================== [CORREO SIMULADO] ====================
                Para:   {}
                Asunto: {}
                {}
                ===========================================================""",
                message.to(), message.subject(), message.body());
    }

    @Override
    public String channel() {
        return "consola";
    }
}
