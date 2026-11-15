package co.unicauca.iso2.taller09.apigateway.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Patrón GoF <b>Adapter</b>: convierte el JWT de Keycloak en la autenticación que entiende
 * Spring Security. Cada rol del realm ({@code AUTOR}, {@code REVISOR}, ...) se traduce a la
 * autoridad {@code ROLE_AUTOR}, {@code ROLE_REVISOR}, ... para poder usar {@code hasRole(...)}
 * en las reglas de autorización del gateway.
 */
public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    static final String ROLE_PREFIX = "ROLE_";

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        KeycloakIdentity identity = KeycloakIdentity.from(jwt);
        List<GrantedAuthority> authorities = identity.roles().stream()
                .<GrantedAuthority>map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role))
                .toList();
        return Mono.just(new JwtAuthenticationToken(jwt, authorities, identity.username()));
    }
}
