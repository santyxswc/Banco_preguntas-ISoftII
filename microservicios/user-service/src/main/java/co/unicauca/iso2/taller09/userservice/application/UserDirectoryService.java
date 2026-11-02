package co.unicauca.iso2.taller09.userservice.application;

import co.unicauca.iso2.taller09.common.enums.Role;
import co.unicauca.iso2.taller09.userservice.domain.User;
import co.unicauca.iso2.taller09.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Service
public class UserDirectoryService {

    private final UserRepository repository;

    public UserDirectoryService(UserRepository repository) {
        this.repository = repository;
    }

    public Optional<User> authenticate(String username, String password) {
        return repository.findByUsername(username).filter(user -> user.validatePassword(password));
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
