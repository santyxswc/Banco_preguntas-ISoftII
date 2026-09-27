/**
 * @file Usuario.java
 * @brief Usuario del sistema.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.domain;

/**
 * @brief Usuario autenticable con un Rol.
 */
public class Usuario {

    private String id;
    private String nombre;
    private String email;
    private String password;
    private Rol rol;

    public Usuario() {
    }

    public Usuario(String id, String nombre, String email,
                   String password, Rol rol) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    @Override
    public String toString() {
        return nombre + " (" + (rol != null ? rol.getEtiqueta() : "-") + ")";
    }
}
