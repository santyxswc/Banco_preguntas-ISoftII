package co.unicauca.iso2.bancopreguntas.presentation;

import co.unicauca.iso2.bancopreguntas.domain.Usuario;

/**
 * Contenedor de la sesión activa del usuario autenticado.
 *
 * Clase estática (patrón "holder de sesión") accesible desde cualquier
 * vista sin necesidad de pasar el usuario por constructor en cada capa.
 * En un sistema real se reemplazaría por un contexto de seguridad
 * (ej. Spring Security SecurityContextHolder).
 */
public final class SessionContext {

    private static Usuario usuarioActual;

    private SessionContext() {
        // No instanciable
    }

    /**
     * Guarda el usuario autenticado en la sesión.
     *
     * @param usuario usuario que ha iniciado sesión
     */
    public static void iniciar(Usuario usuario) {
        usuarioActual = usuario;
    }

    /**
     * Devuelve el usuario autenticado actualmente.
     *
     * @return usuario en sesión, o null si no hay sesión activa
     */
    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Cierra la sesión actual (limpia el usuario guardado).
     */
    public static void cerrar() {
        usuarioActual = null;
    }

    /**
     * Indica si hay una sesión activa.
     *
     * @return true si hay un usuario autenticado
     */
    public static boolean estaAutenticado() {
        return usuarioActual != null;
    }
}
