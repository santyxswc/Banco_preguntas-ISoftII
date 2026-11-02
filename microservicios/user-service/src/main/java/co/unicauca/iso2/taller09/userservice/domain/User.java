package co.unicauca.iso2.taller09.userservice.domain;

import co.unicauca.iso2.taller09.common.dto.UserDTO;
import co.unicauca.iso2.taller09.common.enums.Role;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class User {
    private Long id;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private EnumSet<Role> roles = EnumSet.noneOf(Role.class);

    public User() {
    }

    public User(Long id, String username, String passwordHash, String fullName, String email, Collection<Role> roles) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        setRoles(roles);
    }

    public boolean validatePassword(String plainPassword) {
        return PasswordHasher.matches(plainPassword, passwordHash);
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

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
