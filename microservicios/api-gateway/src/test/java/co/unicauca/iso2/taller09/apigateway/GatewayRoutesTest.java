package co.unicauca.iso2.taller09.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = {
        "QUESTION_SERVICE_ROUTE=http://preguntas:9001",
        "USER_SERVICE_URL=http://usuarios:9003",
        "REVIEW_SERVICE_URL=http://revision:9005"
})
class GatewayRoutesTest {

    @Autowired
    private RouteLocator routeLocator;

    private Map<String, Route> routes() {
        List<Route> list = routeLocator.getRoutes().collectList().block();
        return list.stream().collect(Collectors.toMap(Route::getId, r -> r));
    }

    @Test
    void definesTheFourRoutesRequiredByHuT8() {
        Map<String, Route> routes = routes();
        assertTrue(routes.keySet().containsAll(List.of("preguntas", "usuarios", "asignaciones", "revisiones")));
    }

    @Test
    void takesServiceLocationsFromEnvironment() {
        Map<String, Route> routes = routes();
        assertEquals("http://preguntas:9001", routes.get("preguntas").getUri().toString());
        assertEquals("http://usuarios:9003", routes.get("usuarios").getUri().toString());
        assertEquals("http://revision:9005", routes.get("asignaciones").getUri().toString());
        assertEquals("http://revision:9005", routes.get("revisiones").getUri().toString());
    }

    @Test
    void exposesTheOpenApiDocsOfEveryServiceForTheUnifiedSwaggerUi() {
        Map<String, Route> routes = routes();
        assertTrue(routes.keySet().containsAll(List.of("docs-preguntas", "docs-revisiones", "docs-ciclo-vida",
                "docs-validaciones", "docs-usuarios", "docs-notificaciones")));
        assertEquals("http://preguntas:9001", routes.get("docs-preguntas").getUri().toString());
    }
}
