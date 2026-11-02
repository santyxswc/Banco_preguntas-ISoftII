package co.unicauca.iso2.taller09.userservice.repository.jpa;

import co.unicauca.iso2.taller09.common.enums.Role;
import co.unicauca.iso2.taller09.userservice.domain.User;
import co.unicauca.iso2.taller09.userservice.repository.UserRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class JpaUserRepository implements UserRepository {

    private final UserJpaRepository jpa;

    public JpaUserRepository(UserJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    @Transactional
    public User save(User user) {
        UserEntity saved = jpa.save(new UserEntity(user.getId(), user.getUsername(), user.getPasswordHash(),
                user.getFullName(), user.getEmail(), user.getRoles()));
        return toDomain(saved);
    }

    @Override
    public Optional<User> findById(Long id) {
        return id == null ? Optional.empty() : jpa.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return username == null ? Optional.empty() : jpa.findByUsernameIgnoreCase(username.trim()).map(this::toDomain);
    }

    @Override
    public List<User> findAll() {
        return jpa.findAllByOrderByIdAsc().stream().map(this::toDomain).toList();
    }

    @Override
    public List<User> findByRole(Role role) {
        return jpa.findByRole(role).stream().map(this::toDomain).toList();
    }

    private User toDomain(UserEntity entity) {
        return new User(entity.getId(), entity.getUsername(), entity.getPasswordHash(),
                entity.getFullName(), entity.getEmail(), entity.getRoles());
    }
}
