package co.unicauca.iso2.taller09.notificationservice.email;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/** Envío real por SMTP con Spring Mail (Jakarta Mail). Se activa con {@code notifications.email.mode=smtp}. */
public class SmtpEmailSender implements EmailSender {

    private final JavaMailSender mailSender;
    private final String from;

    public SmtpEmailSender(JavaMailSender mailSender, String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Override
    public void send(EmailMessage message) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(from);
        mail.setTo(message.to());
        mail.setSubject(message.subject());
        mail.setText(message.body());
        try {
            mailSender.send(mail);
        } catch (MailException e) {
            throw new EmailDeliveryException("No se pudo enviar el correo a " + message.to(), e);
        }
    }

    @Override
    public String channel() {
        return "smtp";
    }
}
