package co.unicauca.iso2.taller09.notificationservice.config;

import co.unicauca.iso2.taller09.notificationservice.email.EmailSender;
import co.unicauca.iso2.taller09.notificationservice.email.EmailSenderFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.client.RestTemplate;

/** Elige la fábrica de emisores de correo según {@code notifications.email.mode}. */
@Configuration
public class EmailConfig {

    @Bean
    public EmailSender emailSender(@Value("${notifications.email.mode:console}") String mode,
                                   @Value("${notifications.email.from:banco.preguntas@unicauca.edu.co}") String from,
                                   ObjectProvider<JavaMailSender> mailSender) {
        return EmailSenderFactory.forMode(mode, mailSender.getIfAvailable(), from).sender();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
