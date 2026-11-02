package co.unicauca.iso2.taller09.userservice.repository;

import co.unicauca.iso2.taller09.common.enums.Role;
import co.unicauca.iso2.taller09.userservice.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    List<User> findAll();

    List<User> findByRole(Role role);
}
