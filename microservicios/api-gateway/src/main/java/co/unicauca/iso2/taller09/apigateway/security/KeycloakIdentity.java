package co.unicauca.iso2.taller09.apigateway.security;

import co.unicauca.iso2.taller09.common.enums.Role;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Identidad de un usuario leída de un JWT emitido por Keycloak (RNF #1).
 * <p>
 * Keycloak publica los roles del realm en el claim {@code realm_access.roles}, mezclados con
 * roles técnicos ({@code offline_access}, {@code uma_authorization}, {@code default-roles-*}).
 * Aquí se conservan solo los roles del Banco de Preguntas definidos en {@link Role}.
 */
public record KeycloakIdentity(String subject, String username, String fullName, String email, List<String> roles) {

    private static final Set<String> APPLICATION_ROLES = Arrays.stream(Role.values())
            .map(Role::name).collect(Collectors.toUnmodifiableSet());

    public KeycloakIdentity {
        roles = roles == null ? List.of() : List.copyOf(roles);
    }

    public static KeycloakIdentity from(Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        return new KeycloakIdentity(jwt.getSubject(),
                username == null || username.isBlank() ? jwt.getSubject() : username,
                jwt.getClaimAsString("name"), jwt.getClaimAsString("email"), applicationRoles(jwt));
    }

    /** Roles del realm que pertenecen a la aplicación, en mayúsculas y sin repetidos. */
    public static List<String> applicationRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess == null || !(realmAccess.get("roles") instanceof Collection<?> names)) {
            return List.of();
        }
        return names.stream()
                .filter(String.class::isInstance)
                .map(name -> ((String) name).trim().toUpperCase(Locale.ROOT))
                .filter(APPLICATION_ROLES::contains)
                .distinct()
                .toList();
    }

    public boolean hasRole(Role role) {
        return role != null && roles.contains(role.name());
    }
}
