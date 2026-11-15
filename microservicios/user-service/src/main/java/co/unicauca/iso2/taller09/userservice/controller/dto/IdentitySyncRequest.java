package co.unicauca.iso2.taller09.userservice.controller.dto;

import java.util.List;

/**
 * Datos de identidad que el api-gateway extrae del JWT de Keycloak ({@code preferred_username},
 * {@code name}, {@code email} y los roles del realm) para sincronizar el perfil local.
 */
public record IdentitySyncRequest(String username, String fullName, String email, List<String> roles) {
}
