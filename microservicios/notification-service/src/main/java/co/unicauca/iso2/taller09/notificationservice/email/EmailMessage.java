package co.unicauca.iso2.taller09.notificationservice.email;

/** Correo electrónico a enviar: destinatario, asunto y cuerpo en texto plano. */
public record EmailMessage(String to, String subject, String body) {
}
