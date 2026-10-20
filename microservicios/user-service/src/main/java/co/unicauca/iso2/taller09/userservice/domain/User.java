package co.unicauca.iso2.taller09.userservice.domain;

import co.unicauca.iso2.taller09.common.dto.UserDTO;
import java.util.List;

public class User {
    private Long id;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private List<String> roles;

    public User() {}

    public User(Long id, String username, String passwordHash, String fullName, String email, List<String> roles) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.roles = roles;
    }

    public boolean validatePassword(String plainPassword) {
        if (plainPassword == null || passwordHash == null) return false;
        return passwordHash.equals(plainPassword);
    }

    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    public UserDTO toDTO() {
        return new UserDTO(id, username, fullName, email, roles);
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

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
}
