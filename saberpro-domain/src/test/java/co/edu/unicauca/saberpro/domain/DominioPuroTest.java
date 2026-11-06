/**
 * @file DominioPuroTest.java
 * @brief Verifica que el núcleo del dominio no dependa de frameworks (sin Spring, sin JPA).
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 */
package co.edu.unicauca.saberpro.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @brief Recorre las fuentes del dominio y falla si alguna importa un framework técnico.
 */
class DominioPuroTest {

    private static final Path FUENTES = Path.of("src/main/java/co/edu/unicauca/saberpro/domain");

    private static final List<String> IMPORTS_PROHIBIDOS = List.of(
            "import org.springframework", "import jakarta.persistence", "import javax.persistence",
            "import org.hibernate", "import lombok");

    @Test
    @DisplayName("Dominio puro: ninguna clase importa Spring, JPA, Hibernate ni Lombok")
    void elDominioNoDependeDeFrameworks() throws IOException {
        assertTrue(Files.isDirectory(FUENTES), "No se encontró la carpeta del dominio: " + FUENTES);

        List<String> violaciones;
        try (Stream<Path> archivos = Files.walk(FUENTES)) {
            violaciones = archivos
                    .filter(p -> p.toString().endsWith(".java"))
                    .flatMap(DominioPuroTest::importsProhibidos)
                    .collect(Collectors.toList());
        }
        assertTrue(violaciones.isEmpty(), "Imports técnicos en el dominio: " + violaciones);
    }

    private static Stream<String> importsProhibidos(Path archivo) {
        try {
            return Files.readAllLines(archivo, StandardCharsets.UTF_8).stream()
                    .map(String::trim)
                    .filter(linea -> IMPORTS_PROHIBIDOS.stream().anyMatch(linea::startsWith))
                    .map(linea -> archivo.getFileName() + ": " + linea);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer " + archivo, e);
        }
    }
}
