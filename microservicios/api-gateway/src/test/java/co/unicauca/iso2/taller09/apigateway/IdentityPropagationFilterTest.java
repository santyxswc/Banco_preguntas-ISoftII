package co.unicauca.iso2.taller09.apigateway;

import co.unicauca.iso2.taller09.apigateway.security.IdentityPropagationFilter;
import co.unicauca.iso2.taller09.apigateway.security.KeycloakIdentity;
import co.unicauca.iso2.taller09.apigateway.security.KeycloakJwtAuthenticationConverter;
import co.unicauca.iso2.taller09.apigateway.security.UserIdentityResolver;
import co.unicauca.iso2.taller09.common.constants.IdentityHeaders;
import co.unicauca.iso2.taller09.common.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias del filtro que propaga la identidad del JWT de Keycloak a los servicios.
 * El perfil local se resuelve con un doble del puerto {@link UserIdentityResolver}.
 */
class IdentityPropagationFilterTest {

    private final List<KeycloakIdentity> resolved = new ArrayList<>();
    private UserIdentityResolver resolver = identity -> {
        resolved.add(identity);
        return Mono.just(new UserDTO(4L, identity.username(), "Profesor Carlos Revisor", identity.email(), identity.roles()));
    };

    static Jwt jwt(String username, String... realmRoles) {
        return Jwt.withTokenValue("token-de-prueba")
                .header("alg", "RS256")
                .subject("8f1c2c7e-keycloak-sub")
                .issuer("http://localhost:8180/realms/banco-preguntas")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .claim("preferred_username", username)
                .claim("name", "Profesor Carlos Revisor")
                .claim("email", username + "@unicauca.edu.co")
                .claim("realm_access", Map.of("roles", List.of(realmRoles)))
                .build();
    }

    private ServerHttpRequest run(MockServerHttpRequest.BaseBuilder<?> request, Jwt jwt, AtomicReference<ServerWebExchange> result) {
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        if (jwt != null) {
            Principal principal = new KeycloakJwtAuthenticationConverter().convert(jwt).block();
            exchange = exchange.mutate().principal(Mono.just(principal)).build();
        }
        result.set(exchange);
        AtomicReference<ServerHttpRequest> forwarded = new AtomicReference<>();
        GatewayFilterChain chain = ex -> {
            forwarded.set(ex.getRequest());
            return Mono.empty();
        };
        new IdentityPropagationFilter(resolver).filter(exchange, chain).block();
        return forwarded.get();
    }

    @Test
    @DisplayName("Con un JWT válido agrega la identidad (id local, usuario y roles) para los servicios")
    void conTokenValidoAgregaLaIdentidadParaLosServicios() {
        ServerHttpRequest forwarded = run(MockServerHttpRequest.get("/api/v1/revisiones/asignadas"),
                jwt("revisor1", "REVISOR", "DOCENTE", "offline_access", "default-roles-banco-preguntas"),
                new AtomicReference<>());

        HttpHeaders headers = forwarded.getHeaders();
        assertEquals("4", headers.getFirst(IdentityHeaders.USER_ID));
        assertEquals("revisor1", headers.getFirst(IdentityHeaders.USERNAME));
        assertEquals("REVISOR,DOCENTE", headers.getFirst(IdentityHeaders.USER_ROLES));
        assertEquals(1, resolved.size());
        assertEquals("revisor1@unicauca.edu.co", resolved.get(0).email());
    }

    @Test
    @DisplayName("Las cabeceras de identidad que envía el cliente se reemplazan por las del token")
    void laIdentidadDelTokenReemplazaLaQueEnviaElCliente() {
        ServerHttpRequest forwarded = run(MockServerHttpRequest.get("/api/v1/preguntas")
                        .header(IdentityHeaders.USER_ID, "1")
                        .header(IdentityHeaders.USER_ROLES, "ADMINISTRADOR"),
                jwt("revisor1", "REVISOR"), new AtomicReference<>());

        assertEquals(List.of("4"), forwarded.getHeaders().get(IdentityHeaders.USER_ID));
        assertEquals(List.of("REVISOR"), forwarded.getHeaders().get(IdentityHeaders.USER_ROLES));
    }

    @Test
    @DisplayName("Sin autenticación elimina las cabeceras de identidad falsificadas")
    void eliminaCabecerasDeIdentidadFalsificadasPorElCliente() {
        ServerHttpRequest forwarded = run(MockServerHttpRequest.post("/api/v1/auth/token")
                .header(IdentityHeaders.USER_ID, "1")
                .header(IdentityHeaders.USERNAME, "admin")
                .header(IdentityHeaders.USER_ROLES, "ADMINISTRADOR"), null, new AtomicReference<>());

        assertNotNull(forwarded);
        assertFalse(forwarded.getHeaders().containsKey(IdentityHeaders.USER_ID));
        assertFalse(forwarded.getHeaders().containsKey(IdentityHeaders.USERNAME));
        assertFalse(forwarded.getHeaders().containsKey(IdentityHeaders.USER_ROLES));
        assertTrue(resolved.isEmpty());
    }

    @Test
    @DisplayName("Si user-service no responde, contesta 503 sin llegar al servicio de destino")
    void sinServicioDeUsuariosRespondeNoDisponible() {
        resolver = identity -> Mono.error(new IllegalStateException("user-service caído"));
        AtomicReference<ServerWebExchange> exchange = new AtomicReference<>();

        ServerHttpRequest forwarded = run(MockServerHttpRequest.get("/api/v1/preguntas"), jwt("autor1", "AUTOR"), exchange);

        assertNull(forwarded);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exchange.get().getResponse().getStatusCode());
    }

    @Test
    @DisplayName("Si no se obtiene el perfil local tampoco se propaga una identidad incompleta")
    void sinPerfilLocalNoPropagaIdentidad() {
        resolver = identity -> Mono.empty();
        AtomicReference<ServerWebExchange> exchange = new AtomicReference<>();

        ServerHttpRequest forwarded = run(MockServerHttpRequest.get("/api/v1/preguntas"), jwt("autor1", "AUTOR"), exchange);

        assertNull(forwarded);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exchange.get().getResponse().getStatusCode());
    }
}
