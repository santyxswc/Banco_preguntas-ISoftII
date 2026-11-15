package co.unicauca.iso2.taller09.userservice.domain;

import co.unicauca.iso2.taller09.common.dto.UserDTO;
import co.unicauca.iso2.taller09.common.enums.Role;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Perfil de un usuario del Banco de Preguntas (contexto de Identidad).
 * <p>
 * Desde el corte 3 las credenciales y los roles los administra <b>Keycloak</b> (RNF #1): este
 * servicio ya no guarda contraseñas. Conserva el perfil local —identificador numérico, nombre,
 * correo y roles— que los demás contextos necesitan para asignar revisores y enviar correos, y
 * lo mantiene sincronizado con lo que dice el token JWT ({@link #synchronizeWith}).
 */
public class User {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private EnumSet<Role> roles = EnumSet.noneOf(Role.class);

    public User() {
    }

    public User(Long id, String username, String fullName, String email, Collection<Role> roles) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        setRoles(roles);
    }

    /**
     * Crea el perfil local de un usuario que existe en Keycloak y entra por primera vez.
     * El nombre de usuario se normaliza a minúsculas, igual que lo hace Keycloak.
     */
    public static User fromIdentity(String username, String fullName, String email, Collection<Role> roles) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio");
        }
        String normalized = username.trim().toLowerCase();
        User user = new User(null, normalized, null, null, roles);
        user.fullName = textOr(fullName, normalized);
        user.email = textOr(email, normalized + "@unicauca.edu.co");
        return user;
    }

    /**
     * Actualiza el perfil con los datos del proveedor de identidad, que es la fuente de verdad.
     * Los datos vacíos del token no borran los que ya se tenían.
     *
     * @return {@code true} si algo cambió y hay que guardar el perfil
     */
    public boolean synchronizeWith(String fullName, String email, Collection<Role> roles) {
        String newName = textOr(fullName, this.fullName);
        String newEmail = textOr(email, this.email);
        EnumSet<Role> newRoles = EnumSet.noneOf(Role.class);
        if (roles != null) {
            newRoles.addAll(roles);
        }
        boolean changed = !Objects.equals(newName, this.fullName)
                || !Objects.equals(newEmail, this.email)
                || !newRoles.equals(this.roles);
        this.fullName = newName;
        this.email = newEmail;
        this.roles = newRoles;
        return changed;
    }

    public boolean hasRole(Role role) {
        return role != null && roles.contains(role);
    }

    public boolean canReview(Long authorId) {
        return hasRole(Role.REVISOR) && (authorId == null || !authorId.equals(id));
    }

    public List<String> roleNames() {
        return roles.stream().map(Role::name).toList();
    }

    public UserDTO toDTO() {
        return new UserDTO(id, username, fullName, email, roleNames());
    }

    private static String textOr(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Set<Role> getRoles() { return EnumSet.copyOf(roles); }

    public void setRoles(Collection<Role> roles) {
        EnumSet<Role> copy = EnumSet.noneOf(Role.class);
        if (roles != null) {
            copy.addAll(roles);
        }
        this.roles = copy;
    }
}
