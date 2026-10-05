/**
 * @file BancoPreguntasApplication.java
 * @brief Punto de entrada Spring Boot para el Banco de Preguntas Saber Pro (Corte 2).
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @brief Clase principal de Spring Boot.
 *
 * Habilita la API REST, documentación Swagger/OpenAPI y la arquitectura de microservicios
 * orientada a eventos para el segundo entregable.
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class BancoPreguntasApplication {

    public static void main(String[] args) {
        SpringApplication.run(BancoPreguntasApplication.class, args);
    }
}
