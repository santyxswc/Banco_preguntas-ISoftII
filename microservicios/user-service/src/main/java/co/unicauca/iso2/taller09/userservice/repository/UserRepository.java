package co.unicauca.iso2.taller09.userservice.repository;

import co.unicauca.iso2.taller09.userservice.domain.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserRepository {
    private final Map<Long, User> storage = new ConcurrentHashMap<>();

    public UserRepository() {
        // Seed inicial de usuarios del Banco de Preguntas
        save(new User(1L, "admin", "admin123", "Administrador General", "admin@unicauca.edu.co", List.of("ADMINISTRADOR")));
        save(new User(2L, "autor1", "autor123", "Profesor Juan Pérez (Autor)", "autor1@unicauca.edu.co", List.of("AUTOR", "DOCENTE")));
        save(new User(3L, "autor2", "autor123", "Profesora María Gómez (Autora)", "autor2@unicauca.edu.co", List.of("AUTOR", "DOCENTE")));
        save(new User(4L, "revisor1", "revisor123", "Profesor Carlos Revisor", "revisor1@unicauca.edu.co", List.of("REVISOR", "DOCENTE")));
        save(new User(5L, "revisor2", "revisor123", "Profesora Ana Revisora", "revisor2@unicauca.edu.co", List.of("REVISOR", "DOCENTE")));
        save(new User(6L, "estudiante1", "estudiante123", "Estudiante Pedro", "estudiante1@unicauca.edu.co", List.of("ESTUDIANTE")));
    }

    public User save(User user) {
        storage.put(user.getId(), user);
        return user;
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Optional<User> findByUsername(String username) {
        return storage.values().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    public List<User> findAll() {
        return new ArrayList<>(storage.values());
    }
}
