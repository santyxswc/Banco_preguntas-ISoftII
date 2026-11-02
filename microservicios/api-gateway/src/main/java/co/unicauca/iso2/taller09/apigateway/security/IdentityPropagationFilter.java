package co.unicauca.iso2.taller09.apigateway.security;

import co.unicauca.iso2.taller09.common.constants.IdentityHeaders;
import co.unicauca.iso2.taller09.common.security.AuthTokenCodec;
import co.unicauca.iso2.taller09.common.security.AuthenticatedUser;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class IdentityPropagationFilter implements GlobalFilter, Ordered {

    private final AuthTokenCodec tokens;

    public IdentityPropagationFilter(AuthTokenCodec tokens) {
        this.tokens = tokens;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        Optional<AuthenticatedUser> identity = Optional.empty();
        if (authorization != null && authorization.startsWith(IdentityHeaders.BEARER_PREFIX)) {
            identity = tokens.verify(authorization.substring(IdentityHeaders.BEARER_PREFIX.length()).trim());
            if (identity.isEmpty()) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }
        Optional<AuthenticatedUser> user = identity;
        ServerHttpRequest request = exchange.getRequest().mutate().headers(headers -> {
            headers.remove(IdentityHeaders.USER_ID);
            headers.remove(IdentityHeaders.USERNAME);
            headers.remove(IdentityHeaders.USER_ROLES);
            user.ifPresent(u -> {
                headers.set(IdentityHeaders.USER_ID, String.valueOf(u.userId()));
                headers.set(IdentityHeaders.USERNAME, u.username());
                headers.set(IdentityHeaders.USER_ROLES, String.join(",", u.roles()));
            });
        }).build();
        return chain.filter(exchange.mutate().request(request).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
