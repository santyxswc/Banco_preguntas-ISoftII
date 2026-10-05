/**
 * @file Rol.java
 * @brief Roles de usuario del sistema.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.domain;

/**
 * @brief Rol que determina las opciones que ve cada usuario.
 */
public enum Rol {

    AUTOR("Autor"),
    REVISOR("Revisor"),
    ADMINISTRADOR("Administrador");

    private final String etiqueta;

    Rol(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /** @return nombre legible del rol */
    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
