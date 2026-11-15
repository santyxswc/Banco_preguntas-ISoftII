package co.unicauca.iso2.taller09.apigateway;

import co.unicauca.iso2.taller09.apigateway.security.KeycloakIdentity;
import co.unicauca.iso2.taller09.apigateway.security.KeycloakJwtAuthenticationConverter;
import co.unicauca.iso2.taller09.common.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas unitarias de la lectura de los claims de Keycloak y su traducción a autoridades. */
class KeycloakIdentityTest {

    private static Jwt.Builder token() {
        return Jwt.withTokenValue("t").header("alg", "RS256").subject("sub-123")
                .issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(60));
    }

    @Test
    @DisplayName("Lee usuario, nombre, correo y solo los roles de la aplicación")
    void leeLosClaimsDeKeycloak() {
        Jwt jwt = token().claim("preferred_username", "autor1").claim("name", "Profesor Juan Pérez")
                .claim("email", "autor1@unicauca.edu.co")
                .claim("realm_access", Map.of("roles", List.of("autor", "DOCENTE", "offline_access",
                        "uma_authorization", "default-roles-banco-preguntas", "AUTOR")))
                .build();

        KeycloakIdentity identity = KeycloakIdentity.from(jwt);

        assertEquals("sub-123", identity.subject());
        assertEquals("autor1", identity.username());
        assertEquals("Profesor Juan Pérez", identity.fullName());
        assertEquals("autor1@unicauca.edu.co", identity.email());
        assertEquals(List.of("AUTOR", "DOCENTE"), identity.roles());
        assertTrue(identity.hasRole(Role.AUTOR));
        assertFalse(identity.hasRole(Role.ADMINISTRADOR));
        assertFalse(identity.hasRole(null));
    }

    @Test
    @DisplayName("Un token sin realm_access no tiene roles y usa el sub como nombre de usuario")
    void tokenSinRoles() {
        KeycloakIdentity sinClaim = KeycloakIdentity.from(token().build());
        assertEquals("sub-123", sinClaim.username());
        assertTrue(sinClaim.roles().isEmpty());

        KeycloakIdentity claimMalFormado = KeycloakIdentity.from(token()
                .claim("realm_access", Map.of("roles", "AUTOR")).build());
        assertTrue(claimMalFormado.roles().isEmpty());
        assertTrue(new KeycloakIdentity("s", "u", null, null, null).roles().isEmpty());
    }

    @Test
    @DisplayName("El convertidor traduce cada rol del realm a la autoridad ROLE_<rol>")
    void convierteRolesEnAutoridades() {
        Jwt jwt = token().claim("preferred_username", "admin")
                .claim("realm_access", Map.of("roles", List.of("ADMINISTRADOR", "offline_access"))).build();

        AbstractAuthenticationToken authentication = new KeycloakJwtAuthenticationConverter().convert(jwt).block();

        assertEquals("admin", authentication.getName());
        assertEquals(List.of("ROLE_ADMINISTRADOR"),
                authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
    }
}
