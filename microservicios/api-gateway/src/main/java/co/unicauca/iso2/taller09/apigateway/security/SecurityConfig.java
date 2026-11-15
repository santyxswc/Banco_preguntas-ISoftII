package co.unicauca.iso2.taller09.apigateway.security;

import co.unicauca.iso2.taller09.common.enums.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * Seguridad del punto de entrada (RNF #1): el api-gateway es el <b>Resource Server OAuth2</b>
 * del sistema. Valida cada JWT contra las llaves públicas (JWKS) de Keycloak y aplica la
 * autorización por rol antes de enrutar la petición a un microservicio.
 * <p>
 * Reglas:
 * <ul>
 *   <li>Públicas: obtener el token ({@code /api/v1/auth/**}), Swagger y la sonda de salud.</li>
 *   <li>AUTOR: crear, editar y enviar preguntas a revisión (HU-01, HU-02, HU-03, HU-06).</li>
 *   <li>ADMINISTRADOR: asignar revisores, consultar usuarios y forzar transiciones (HU-04).</li>
 *   <li>REVISOR: listar sus preguntas asignadas y evaluarlas (HU-05).</li>
 *   <li>Cualquier otra ruta exige un token válido.</li>
 * </ul>
 * Sin token el gateway responde 401 y con un rol insuficiente 403, ambos en JSON.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final String ADMINISTRADOR = Role.ADMINISTRADOR.name();
    private static final String AUTOR = Role.AUTOR.name();
    private static final String REVISOR = Role.REVISOR.name();

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(Customizer.withDefaults())
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .pathMatchers("/api/v1/auth/**").permitAll()
                        .pathMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/webjars/**",
                                "/docs/**", "/actuator/health/**", "/actuator/info").permitAll()

                        .pathMatchers(HttpMethod.POST, "/api/v1/preguntas").hasRole(AUTOR)
                        .pathMatchers(HttpMethod.PUT, "/api/v1/preguntas/*").hasRole(AUTOR)
                        .pathMatchers(HttpMethod.POST, "/api/v1/preguntas/*/submit-review").hasRole(AUTOR)

                        .pathMatchers("/api/v1/asignaciones", "/api/v1/asignaciones/**").hasRole(ADMINISTRADOR)
                        .pathMatchers(HttpMethod.GET, "/api/v1/usuarios/me").authenticated()
                        .pathMatchers("/api/v1/usuarios", "/api/v1/usuarios/**").hasRole(ADMINISTRADOR)
                        .pathMatchers(HttpMethod.POST, "/api/v1/ciclo-vida/**").hasRole(ADMINISTRADOR)
                        .pathMatchers("/actuator/**").hasRole(ADMINISTRADOR)

                        .pathMatchers(HttpMethod.GET, "/api/v1/revisiones/asignadas").hasRole(REVISOR)
                        .pathMatchers(HttpMethod.POST, "/api/v1/revisiones/evaluar").hasRole(REVISOR)

                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(new KeycloakJwtAuthenticationConverter()))
                        .authenticationEntryPoint(unauthorized())
                        .accessDeniedHandler(forbidden()))
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(unauthorized())
                        .accessDeniedHandler(forbidden()))
                .build();
    }

    /** CORS en la cadena de seguridad para que las respuestas 401 y 403 también lleguen al navegador. */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(@Value("${FRONTEND_ORIGINS:*}") String origins) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.stream(origins.split(",")).map(String::trim).toList());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private static ServerAuthenticationEntryPoint unauthorized() {
        return (exchange, e) -> writeError(exchange, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED",
                "Debe iniciar sesión: falta el token JWT o no es válido.");
    }

    private static ServerAccessDeniedHandler forbidden() {
        return (exchange, e) -> writeError(exchange, HttpStatus.FORBIDDEN, "FORBIDDEN",
                "Su rol no tiene permiso para realizar esta operación.");
    }

    private static Mono<Void> writeError(ServerWebExchange exchange, HttpStatus status, String code, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] body = ("{\"error\":\"" + code + "\",\"message\":\"" + message + "\"}").getBytes(StandardCharsets.UTF_8);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body)));
    }
}
