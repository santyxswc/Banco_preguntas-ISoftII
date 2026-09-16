package co.unicauca.iso2.bancopreguntas.domain;

/**
 * Roles de usuario en el sistema de banco de preguntas.
 */
public enum Rol {

    AUTOR("Autor"),
    ADMINISTRADOR("Administrador");

    private final String etiqueta;

    Rol(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
