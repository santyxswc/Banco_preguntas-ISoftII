package co.unicauca.iso2.taller09.notificationservice.application;

/** Puerto hacia el contexto de Identidad: obtiene el correo de un usuario a partir de su id. */
public interface UserDirectory {

    String emailOf(Long userId);
}
