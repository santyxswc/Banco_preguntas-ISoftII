/**
 * @file OpenApiConfig.java
 * @brief Configuración de OpenAPI 3 / Swagger para documentar la API REST.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.infra.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @brief Configura la especificación OpenAPI visible en /swagger-ui.html.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bancoPreguntasOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Banco de Preguntas Saber Pro — API REST")
                        .description("API REST para la gestión, validación estructural, ciclo de vida, asignación y revisión por pares de preguntas Saber Pro (Ingeniería de Software II — Segundo Corte).")
                        .version("2.0.0")
                        .contact(new Contact()
                                .name("Universidad del Cauca — Equipo de Desarrollo")
                                .email("banco-preguntas@unicauca.edu.co"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
