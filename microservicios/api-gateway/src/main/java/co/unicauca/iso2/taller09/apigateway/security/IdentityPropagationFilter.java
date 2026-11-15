package co.unicauca.iso2.taller09.apigateway.security;

import co.unicauca.iso2.taller09.common.constants.IdentityHeaders;
import co.unicauca.iso2.taller09.common.dto.UserDTO;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Propaga la identidad del usuario autenticado hacia los microservicios (RNF #1).
 * <p>
 * Spring Security ya validó la firma, el emisor y la vigencia del JWT de Keycloak antes de
 * llegar aquí. Este filtro: (1) elimina las cabeceras {@code X-User-*} que el cliente haya
 * intentado falsificar, (2) resuelve el perfil local del usuario y (3) agrega
 * {@code X-User-Id}, {@code X-User-Name} y {@code X-User-Roles}, que es lo que leen los
 * servicios para saber quién hace la petición. Los servicios no quedan expuestos fuera de la
 * red interna, por lo que esas cabeceras solo puede escribirlas el gateway.
 */
@Component
public class IdentityPropagationFilter implements GlobalFilter, Ordered {

    private final UserIdentityResolver resolver;

    public IdentityPropagationFilter(UserIdentityResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return exchange.getPrincipal()
                .filter(JwtAuthenticationToken.class::isInstance)
                .cast(JwtAuthenticationToken.class)
                .map(authentication -> KeycloakIdentity.from(authentication.getToken()))
                .flatMap(identity -> resolver.resolve(identity)
                        .onErrorMap(IdentityUnavailableException::new)
                        .switchIfEmpty(Mono.error(IdentityUnavailableException::new))
                        .map(user -> forward(exchange, identity, user)))
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty())
                .flatMap(authenticated -> chain.filter(authenticated.orElseGet(() -> anonymous(exchange))))
                .onErrorResume(IdentityUnavailableException.class, e -> unavailable(exchange));
    }

    private static ServerWebExchange forward(ServerWebExchange exchange, KeycloakIdentity identity, UserDTO user) {
        if (user.getId() == null) {
            throw new IdentityUnavailableException();
        }
        ServerHttpRequest request = exchange.getRequest().mutate().headers(headers -> {
            removeIdentity(headers);
            headers.set(IdentityHeaders.USER_ID, String.valueOf(user.getId()));
            headers.set(IdentityHeaders.USERNAME, identity.username());
            headers.set(IdentityHeaders.USER_ROLES, String.join(",", identity.roles()));
        }).build();
        return exchange.mutate().request(request).build();
    }

    private static ServerWebExchange anonymous(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest().mutate()
                .headers(IdentityPropagationFilter::removeIdentity).build();
        return exchange.mutate().request(request).build();
    }

    private static void removeIdentity(HttpHeaders headers) {
        headers.remove(IdentityHeaders.USER_ID);
        headers.remove(IdentityHeaders.USERNAME);
        headers.remove(IdentityHeaders.USER_ROLES);
    }

    private static Mono<Void> unavailable(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] body = "{\"error\":\"IDENTITY_UNAVAILABLE\",\"message\":\"El servicio de usuarios no está disponible; intente de nuevo.\"}"
                .getBytes(StandardCharsets.UTF_8);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body)));
    }

    /** El perfil local no se pudo determinar: sin identificador no se puede propagar la identidad. */
    static class IdentityUnavailableException extends RuntimeException {
        IdentityUnavailableException() {
        }

        IdentityUnavailableException(Throwable cause) {
            super(cause);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
