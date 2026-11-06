/**
 * @file PreguntaMemoryRepositoryTest.java
 * @brief Pruebas del contrato de repositorio de dominio con implementación en memoria.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 */
package co.edu.unicauca.saberpro.domain.repository;

import co.edu.unicauca.saberpro.domain.model.Enunciado;
import co.edu.unicauca.saberpro.domain.model.EstadoPregunta;
import co.edu.unicauca.saberpro.domain.model.Justificacion;
import co.edu.unicauca.saberpro.domain.model.Pregunta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @brief Pruebas del contrato de repositorio puro en memoria.
 */
class PreguntaMemoryRepositoryTest {

    private PreguntaRepository repository;

    @BeforeEach
    void setUp() {
        repository = new PreguntaMemoryRepository();
    }

    private Pregunta crearPreguntaValida() {
        Pregunta p = new Pregunta(
                null,
                "docente-1",
                new Enunciado("¿Cuál es la función principal de un repositorio en DDD?"),
                new Justificacion("El repositorio simula una colección en memoria para persistir y recuperar agregados.")
        );
        p.agregarOpcion("Abstraer la persistencia y coleccionar agregados", true);
        p.agregarOpcion("Conectarse directamente con vistas de interfaz gráfica", false);
        p.agregarOpcion("Reemplazar los controladores REST del sistema", false);
        p.agregarOpcion("Ejecutar scripts DDL en la base de datos relacional", false);
        return p;
    }

    @Test
    @DisplayName("Guarda y recupera pregunta generando identificador automático")
    void guardarYRecuperar() {
        Pregunta p = crearPreguntaValida();
        Pregunta guardada = repository.save(p);

        assertNotNull(guardada.getId());
        Optional<Pregunta> encontrada = repository.findById(guardada.getId());
        assertTrue(encontrada.isPresent());
        assertEquals("docente-1", encontrada.get().getAutorId());
    }

    @Test
    @DisplayName("Filtra preguntas por estado en el repositorio")
    void filtrarPorEstado() {
        Pregunta p1 = crearPreguntaValida();
        repository.save(p1);

        Pregunta p2 = crearPreguntaValida();
        p2.enviarARevision();
        repository.save(p2);

        List<Pregunta> borradores = repository.findByEstado(EstadoPregunta.BORRADOR);
        List<Pregunta> pendientes = repository.findByEstado(EstadoPregunta.PENDIENTE_REVISION);

        assertEquals(1, borradores.size());
        assertEquals(1, pendientes.size());
    }

    @Test
    @DisplayName("Eliminar pregunta del repositorio")
    void eliminarPregunta() {
        Pregunta p = repository.save(crearPreguntaValida());
        Long id = p.getId();

        assertTrue(repository.findById(id).isPresent());
        repository.deleteById(id);
        assertFalse(repository.findById(id).isPresent());
    }
}
