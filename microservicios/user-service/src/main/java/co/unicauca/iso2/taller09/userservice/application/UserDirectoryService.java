package co.unicauca.iso2.taller09.userservice.application;

import co.unicauca.iso2.taller09.common.enums.Role;
import co.unicauca.iso2.taller09.userservice.domain.User;
import co.unicauca.iso2.taller09.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * Casos de uso del contexto de Identidad: consulta de usuarios y roles, revisores elegibles y
 * sincronización del perfil local con el proveedor de identidad (Keycloak, RNF #1).
 */
@Service
public class UserDirectoryService {

    private final UserRepository repository;

    public UserDirectoryService(UserRepository repository) {
        this.repository = repository;
    }

    /**
     * Aprovisionamiento "justo a tiempo": el api-gateway llama a este caso de uso con los datos
     * del JWT ya validado. Si el usuario no existe se crea su perfil local; si existe se
     * actualizan nombre, correo y roles, porque Keycloak es la fuente de verdad. Los roles del
     * token que no pertenecen al sistema (por ejemplo {@code offline_access}) se ignoran.
     */
    public User synchronize(String username, String fullName, String email, Collection<String> roleNames) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio");
        }
        Set<Role> roles = knownRoles(roleNames);
        Optional<User> existing = repository.findByUsername(username);
        if (existing.isEmpty()) {
            return repository.save(User.fromIdentity(username, fullName, email, roles));
        }
        User user = existing.get();
        return user.synchronizeWith(fullName, email, roles) ? repository.save(user) : user;
    }

    private static Set<Role> knownRoles(Collection<String> roleNames) {
        Set<Role> roles = EnumSet.noneOf(Role.class);
        if (roleNames != null) {
            for (String name : roleNames) {
                if (name == null) {
                    continue;
                }
                try {
                    roles.add(Role.valueOf(name.trim().toUpperCase(Locale.ROOT)));
                } catch (IllegalArgumentException ignored) {
                    // rol técnico de Keycloak, no es un rol del Banco de Preguntas
                }
            }
        }
        return roles;
    }

    public List<User> all() {
        return repository.findAll();
    }

    public Optional<User> byId(Long id) {
        return repository.findById(id);
    }

    public List<User> byRole(String roleName) {
        return repository.findByRole(parseRole(roleName));
    }

    public List<User> eligibleReviewers(Long authorId, Collection<Long> excluding) {
        Set<Long> excluded = excluding == null ? Set.of() : Set.copyOf(excluding);
        return repository.findByRole(Role.REVISOR).stream()
                .filter(user -> user.canReview(authorId))
                .filter(user -> !excluded.contains(user.getId()))
                .toList();
    }

    public static Role parseRole(String roleName) {
        if (roleName == null || roleName.isBlank()) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }
        try {
            return Role.valueOf(roleName.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Rol desconocido: " + roleName);
        }
    }
}
