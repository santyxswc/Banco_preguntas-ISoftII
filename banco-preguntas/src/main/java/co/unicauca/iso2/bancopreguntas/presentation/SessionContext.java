/**
 * @file SessionContext.java
 * @brief Sesión del usuario autenticado.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.presentation;

import co.unicauca.iso2.bancopreguntas.domain.Usuario;

/**
 * @brief Guarda el usuario que inició sesión para que las vistas lo
 *        consulten.
 */
public final class SessionContext {

    private static Usuario usuarioActual;

    private SessionContext() {
    }

    /** @param usuario usuario que inició sesión */
    public static void iniciar(Usuario usuario) {
        usuarioActual = usuario;
    }

    /** @return usuario en sesión o null */
    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /** @brief Cierra la sesión. */
    public static void cerrar() {
        usuarioActual = null;
    }

    /** @return true si hay un usuario autenticado */
    public static boolean estaAutenticado() {
        return usuarioActual != null;
    }
}
