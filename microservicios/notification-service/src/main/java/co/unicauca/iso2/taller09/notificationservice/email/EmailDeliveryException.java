package co.unicauca.iso2.taller09.notificationservice.email;

/** El correo no pudo entregarse (servidor SMTP caído, credenciales inválidas, etc.). */
public class EmailDeliveryException extends RuntimeException {

    public EmailDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
