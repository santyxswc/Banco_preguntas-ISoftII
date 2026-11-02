package co.unicauca.iso2.taller09.notificationservice.email;

/**
 * Producto del Factory Method y componente del Decorator: cualquier forma de entregar un correo.
 * Lanza {@link EmailDeliveryException} si no se pudo entregar.
 */
public interface EmailSender {

    void send(EmailMessage message);

    /** Nombre del canal, para trazas ("consola", "smtp"). */
    String channel();
}
