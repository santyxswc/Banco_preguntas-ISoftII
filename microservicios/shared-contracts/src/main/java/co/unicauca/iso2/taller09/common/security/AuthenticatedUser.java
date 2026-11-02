package co.unicauca.iso2.taller09.common.security;

import java.time.Instant;
import java.util.List;

public record AuthenticatedUser(Long userId, String username, List<String> roles, Instant expiresAt) {

    public AuthenticatedUser {
        roles = roles == null ? List.of() : List.copyOf(roles);
    }

    public boolean hasRole(String role) {
        return role != null && roles.stream().anyMatch(r -> r.equalsIgnoreCase(role));
    }
}
