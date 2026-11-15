package co.unicauca.iso2.taller09.reviewservice;

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
 * Reglas de la arquitectura hexagonal (RNF #2): las dependencias apuntan hacia adentro
 * y el núcleo del dominio no conoce frameworks ni capas externas.
 */
class ArquitecturaHexagonalTest {

    private static final Path BASE = Path.of("src/main/java/co/unicauca/iso2/taller09/reviewservice");
    private static final Path DOMINIO = BASE.resolve("domain");
    private static final Path APLICACION = BASE.resolve("application");

    private static final List<String> FRAMEWORKS = List.of(
            "import org.springframework",
            "import jakarta.persistence",
            "import javax.persistence",
            "import org.hibernate",
            "import lombok",
            "import com.fasterxml.jackson");

    @Test
    @DisplayName("El dominio no importa Spring, JPA, Hibernate, Lombok ni Jackson")
    void dominioSinFrameworks() throws IOException {
        assertSinViolaciones(violaciones(DOMINIO, FRAMEWORKS),
                "El núcleo del dominio no debe depender de frameworks técnicos");
    }

    @Test
    @DisplayName("El dominio no depende de application, infrastructure ni presentation")
    void dominioIndependienteDeCapas() throws IOException {
        assertSinViolaciones(violaciones(DOMINIO, List.of(
                "import co.unicauca.iso2.taller09.reviewservice.application",
                "import co.unicauca.iso2.taller09.reviewservice.infrastructure",
                "import co.unicauca.iso2.taller09.reviewservice.presentation")),
                "El dominio no debe depender de capas externas");
    }

    @Test
    @DisplayName("La aplicación habla solo con puertos: no depende de infrastructure ni presentation")
    void aplicacionSoloDependeDePuertos() throws IOException {
        assertSinViolaciones(violaciones(APLICACION, List.of(
                "import co.unicauca.iso2.taller09.reviewservice.infrastructure",
                "import co.unicauca.iso2.taller09.reviewservice.presentation")),
                "Los casos de uso deben depender de abstracciones (puertos), no de adaptadores");
    }

    private static void assertSinViolaciones(List<String> violaciones, String mensaje) {
        assertTrue(violaciones.isEmpty(), mensaje + " -> " + violaciones);
    }

    private static List<String> violaciones(Path carpeta, List<String> prefijosProhibidos) throws IOException {
        assertTrue(Files.isDirectory(carpeta), "No se encontró la carpeta: " + carpeta);
        try (Stream<Path> archivos = Files.walk(carpeta)) {
            return archivos
                    .filter(p -> p.toString().endsWith(".java"))
                    .flatMap(p -> lineasProhibidas(p, prefijosProhibidos))
                    .collect(Collectors.toList());
        }
    }

    private static Stream<String> lineasProhibidas(Path archivo, List<String> prefijos) {
        try {
            return Files.readAllLines(archivo, StandardCharsets.UTF_8).stream()
                    .map(String::trim)
                    .filter(linea -> prefijos.stream().anyMatch(linea::startsWith))
                    .map(linea -> archivo.getFileName() + ": " + linea);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer " + archivo, e);
        }
    }
}
