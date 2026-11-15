package co.unicauca.iso2.taller09.common.security;

import co.unicauca.iso2.taller09.common.enums.Role;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Identidad de quien hace la petición, tal como la propaga el api-gateway en las cabeceras
 * {@code X-User-Id} y {@code X-User-Roles} después de validar el JWT de Keycloak (RNF #1).
 * <p>
 * Los servicios la usan para las reglas de autorización que dependen del dato y no solo del
 * rol: un autor solo lista sus preguntas y cada usuario solo lee sus notificaciones. Una
 * petición sin identidad es una llamada interna entre servicios, que no pasa por el gateway.
 */
public record RequestIdentity(Long userId, List<String> roles) {

    public RequestIdentity {
        roles = roles == null ? List.of() : List.copyOf(roles);
    }

    /** Construye la identidad a partir de las cabeceras; {@code rolesHeader} viene separado por comas. */
    public static RequestIdentity of(Long userId, String rolesHeader) {
        List<String> roles = rolesHeader == null || rolesHeader.isBlank()
                ? List.of()
                : Arrays.stream(rolesHeader.split(",")).map(String::trim).filter(r -> !r.isEmpty()).toList();
        return new RequestIdentity(userId, roles);
    }

    public boolean isAnonymous() {
        return userId == null;
    }

    public boolean hasRole(Role role) {
        return role != null && roles.stream().anyMatch(r -> r.equalsIgnoreCase(role.name()));
    }

    public boolean isAdministrator() {
        return hasRole(Role.ADMINISTRADOR);
    }

    /** Verdadero si puede ver los datos de {@code ownerId}: es su dueño, un administrador o una llamada interna. */
    public boolean canAccessDataOf(Long ownerId) {
        return isAnonymous() || isAdministrator() || Objects.equals(userId, ownerId);
    }

    /**
     * Dueño por el que se debe filtrar un listado: el administrador (y las llamadas internas)
     * conservan el filtro pedido; los demás usuarios solo ven lo propio.
     */
    public Long ownerFilter(Long requestedOwnerId) {
        return isAnonymous() || isAdministrator() ? requestedOwnerId : userId;
    }
}
