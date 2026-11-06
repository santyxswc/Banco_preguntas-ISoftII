/**
 * @file EntidadesRevisionTest.java
 * @brief Pruebas unitarias de las entidades Revisor y Revision del dominio Saber Pro (DDD).
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 */
package co.edu.unicauca.saberpro.domain.model;

import co.edu.unicauca.saberpro.domain.exception.ReglaNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @brief Pruebas de identidad de las entidades y de las reglas de la revisión por pares.
 */
class EntidadesRevisionTest {

    private Pregunta pregunta;

    @BeforeEach
    void setUp() {
        pregunta = new Pregunta(1L, "DOC-AUTOR", new Enunciado("¿Qué patrón desacopla emisor y receptor?"),
                new Justificacion("El patrón Observer permite notificar sin conocer al receptor."));
    }

    @Test
    @DisplayName("Revisor: la igualdad es por ID de docente, no por nombre")
    void revisorIgualdadPorIdentidad() {
        Revisor r1 = new Revisor("DOC-1", "Ana Pérez");
        Revisor r2 = new Revisor("DOC-1", "Ana P.");
        Revisor r3 = new Revisor("DOC-2", "Ana Pérez");

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1, r3);
        assertThrows(ReglaNegocioException.class, () -> new Revisor(" ", "Sin ID"));
    }

    @Test
    @DisplayName("Revision: el autor de la pregunta no puede ser su revisor")
    void autorNoPuedeSerRevisor() {
        Revisor autor = new Revisor("DOC-AUTOR", "Autor");
        assertThrows(ReglaNegocioException.class, () -> new Revision(10L, pregunta, autor));
    }

    @Test
    @DisplayName("Revision: un rechazo exige observación y el dictamen se emite una sola vez")
    void dictamenUnicoYRechazoConObservacion() {
        Revision revision = new Revision(10L, pregunta, new Revisor("DOC-1", "Ana Pérez"));

        assertThrows(ReglaNegocioException.class, () -> revision.emitirDictamen(Dictamen.RECHAZADA, " "));

        revision.emitirDictamen(Dictamen.RECHAZADA, "Los distractores no son plausibles.");
        assertEquals(Dictamen.RECHAZADA, revision.getDictamen().orElseThrow());
        assertTrue(revision.getObservacion().isPresent());

        assertThrows(ReglaNegocioException.class, () -> revision.emitirDictamen(Dictamen.APROBADA, null));
    }
}
