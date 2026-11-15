package co.unicauca.iso2.taller09.apigateway;

import co.unicauca.iso2.taller09.apigateway.security.UserIdentityResolver;
import co.unicauca.iso2.taller09.common.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

/**
 * Reglas de autenticación y autorización del gateway (RNF #1): sin token 401, con un rol
 * insuficiente 403 y con el rol correcto la petición se enruta. Los servicios de destino no
 * están levantados (puerto 1), así que una petición autorizada termina en un error de conexión:
 * lo que se comprueba es que la seguridad no la haya detenido.
 */
@SpringBootTest(properties = {
        "QUESTION_SERVICE_ROUTE=http://127.0.0.1:1",
        "USER_SERVICE_URL=http://127.0.0.1:1",
        "REVIEW_SERVICE_URL=http://127.0.0.1:1",
        "LIFECYCLE_SERVICE_URL=http://127.0.0.1:1",
        "KEYCLOAK_URL=http://127.0.0.1:1"
})
@AutoConfigureWebTestClient
class GatewaySecurityTest {

    private static final String PREGUNTA = "{\"text\":\"¿Qué patrón construye objetos paso a paso?\"}";

    @Autowired
    private WebTestClient client;

    @MockBean
    private UserIdentityResolver resolver;

    @BeforeEach
    void perfilLocal() {
        when(resolver.resolve(any())).thenReturn(Mono.just(new UserDTO(2L, "usuario", "Usuario", "u@unicauca.edu.co", List.of())));
    }

    private WebTestClient conRol(String... roles) {
        return client.mutateWith(mockJwt()
                .jwt(jwt -> jwt.claim("preferred_username", "usuario"))
                .authorities(Arrays.stream(roles).<GrantedAuthority>map(r -> new SimpleGrantedAuthority("ROLE_" + r)).toList()));
    }

    private static void enrutada(int status) {
        assertTrue(status != 401 && status != 403, "La seguridad no debía detener la petición, respondió " + status);
    }

    @Test
    @DisplayName("Sin token la API responde 401 con un mensaje en JSON")
    void sinTokenRespondeNoAutenticado() {
        client.get().uri("/api/v1/preguntas").exchange()
                .expectStatus().isUnauthorized()
                .expectBody().jsonPath("$.error").isEqualTo("UNAUTHORIZED");
        client.get().uri("/api/v1/revisiones/pregunta/1/historial").exchange().expectStatus().isUnauthorized();
        client.get().uri("/api/v1/notificaciones/recipient/1").exchange().expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("Un token inválido también se rechaza con 401")
    void tokenInvalidoRespondeNoAutenticado() {
        client.get().uri("/api/v1/preguntas").header("Authorization", "Bearer token-alterado").exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("La obtención del token y Swagger son públicos")
    void rutasPublicas() {
        client.post().uri("/api/v1/auth/token").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("grant_type=password").exchange().expectStatus().value(GatewaySecurityTest::enrutada);
        client.get().uri("/v3/api-docs/swagger-config").exchange().expectStatus().isOk();
        client.get().uri("/actuator/health").exchange().expectStatus().value(GatewaySecurityTest::enrutada);
    }

    @Test
    @DisplayName("Solo el AUTOR crea, edita y envía preguntas a revisión")
    void soloElAutorEscribePreguntas() {
        conRol("REVISOR").post().uri("/api/v1/preguntas").contentType(MediaType.APPLICATION_JSON).bodyValue(PREGUNTA)
                .exchange().expectStatus().isForbidden().expectBody().jsonPath("$.error").isEqualTo("FORBIDDEN");
        conRol("ADMINISTRADOR").put().uri("/api/v1/preguntas/7").contentType(MediaType.APPLICATION_JSON).bodyValue(PREGUNTA)
                .exchange().expectStatus().isForbidden();
        conRol("ESTUDIANTE").post().uri("/api/v1/preguntas/7/submit-review").exchange().expectStatus().isForbidden();

        conRol("AUTOR").post().uri("/api/v1/preguntas").contentType(MediaType.APPLICATION_JSON).bodyValue(PREGUNTA)
                .exchange().expectStatus().value(GatewaySecurityTest::enrutada);
        conRol("AUTOR").post().uri("/api/v1/preguntas/7/submit-review").exchange()
                .expectStatus().value(GatewaySecurityTest::enrutada);
    }

    @Test
    @DisplayName("Solo el ADMINISTRADOR asigna revisores y consulta usuarios")
    void soloElAdministradorAsignaYConsultaUsuarios() {
        conRol("AUTOR").post().uri("/api/v1/asignaciones").contentType(MediaType.APPLICATION_JSON).bodyValue("{}")
                .exchange().expectStatus().isForbidden();
        conRol("REVISOR").get().uri("/api/v1/usuarios/reviewers/eligible").exchange().expectStatus().isForbidden();
        conRol("AUTOR").post().uri("/api/v1/ciclo-vida/7/transition").contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"targetState\":\"APPROVED\"}").exchange().expectStatus().isForbidden();

        conRol("ADMINISTRADOR").post().uri("/api/v1/asignaciones").contentType(MediaType.APPLICATION_JSON).bodyValue("{}")
                .exchange().expectStatus().value(GatewaySecurityTest::enrutada);
        conRol("ADMINISTRADOR").get().uri("/api/v1/usuarios").exchange().expectStatus().value(GatewaySecurityTest::enrutada);
    }

    @Test
    @DisplayName("Solo el REVISOR lista sus asignadas y evalúa")
    void soloElRevisorEvalua() {
        conRol("AUTOR").get().uri("/api/v1/revisiones/asignadas").exchange().expectStatus().isForbidden();
        conRol("ADMINISTRADOR").post().uri("/api/v1/revisiones/evaluar").contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}").exchange().expectStatus().isForbidden();

        conRol("REVISOR").get().uri("/api/v1/revisiones/asignadas").exchange()
                .expectStatus().value(GatewaySecurityTest::enrutada);
    }

    @Test
    @DisplayName("Cualquier usuario autenticado consulta preguntas, su perfil y el historial de revisiones")
    void lecturasParaCualquierAutenticado() {
        conRol("ESTUDIANTE").get().uri("/api/v1/preguntas").exchange().expectStatus().value(GatewaySecurityTest::enrutada);
        conRol("AUTOR").get().uri("/api/v1/usuarios/me").exchange().expectStatus().value(GatewaySecurityTest::enrutada);
        conRol("AUTOR").get().uri("/api/v1/revisiones/pregunta/7/historial").exchange()
                .expectStatus().value(GatewaySecurityTest::enrutada);
    }
}
