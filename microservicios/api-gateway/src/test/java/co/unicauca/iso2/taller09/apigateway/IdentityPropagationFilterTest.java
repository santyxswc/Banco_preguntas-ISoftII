package co.unicauca.iso2.taller09.apigateway;

import co.unicauca.iso2.taller09.apigateway.security.IdentityPropagationFilter;
import co.unicauca.iso2.taller09.common.constants.IdentityHeaders;
import co.unicauca.iso2.taller09.common.security.AuthTokenCodec;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class IdentityPropagationFilterTest {

    private final AuthTokenCodec codec = new AuthTokenCodec("secreto-de-pruebas-gateway", Duration.ofMinutes(10));
    private final IdentityPropagationFilter filter = new IdentityPropagationFilter(codec);

    private ServerHttpRequest run(MockServerWebExchange exchange) {
        AtomicReference<ServerHttpRequest> forwarded = new AtomicReference<>();
        GatewayFilterChain chain = ex -> {
            forwarded.set(ex.getRequest());
            return Mono.empty();
        };
        filter.filter(exchange, chain).block();
        return forwarded.get();
    }

    @Test
    void conTokenValidoAgregaLaIdentidadParaLosServicios() {
        String token = codec.issue(4L, "revisor1", List.of("REVISOR", "DOCENTE"));
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/v1/revisiones/asignadas")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token));

        HttpHeaders headers = run(exchange).getHeaders();

        assertEquals("4", headers.getFirst(IdentityHeaders.USER_ID));
        assertEquals("revisor1", headers.getFirst(IdentityHeaders.USERNAME));
        assertEquals("REVISOR,DOCENTE", headers.getFirst(IdentityHeaders.USER_ROLES));
    }

    @Test
    void eliminaCabecerasDeIdentidadFalsificadasPorElCliente() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/v1/preguntas")
                .header(IdentityHeaders.USER_ID, "1")
                .header(IdentityHeaders.USER_ROLES, "ADMINISTRADOR"));

        HttpHeaders headers = run(exchange).getHeaders();

        assertFalse(headers.containsKey(IdentityHeaders.USER_ID));
        assertFalse(headers.containsKey(IdentityHeaders.USER_ROLES));
    }

    @Test
    void conTokenInvalidoRespondeNoAutorizadoSinLlegarAlServicio() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/v1/preguntas")
                .header(HttpHeaders.AUTHORIZATION, "Bearer token-alterado"));

        ServerHttpRequest forwarded = run(exchange);

        assertNull(forwarded);
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }
}
