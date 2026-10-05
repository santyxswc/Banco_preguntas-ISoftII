/**
 * @file RevisionTest.java
 * @brief Pruebas unitarias para la entidad Revision (HU05).
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RevisionTest {

    @Test
    void constructorPorDefectoInicializaFecha() {
        Revision rev = new Revision();
        assertNotNull(rev.getFecha());
    }

    @Test
    void constructorCompletoAsignaTodosLosCampos() {
        LocalDateTime ahora = LocalDateTime.now();
        Revision rev = new Revision("REV-001", "PREG-001", "rev1",
                EstadoPregunta.APROBADA, "Excelente formulación", ahora);

        assertEquals("REV-001", rev.getId());
        assertEquals("PREG-001", rev.getPreguntaId());
        assertEquals("rev1", rev.getRevisorId());
        assertEquals(EstadoPregunta.APROBADA, rev.getVeredicto());
        assertEquals("Excelente formulación", rev.getObservaciones());
        assertEquals(ahora, rev.getFecha());
    }

    @Test
    void settersModificanValoresCorrectamente() {
        Revision rev = new Revision();
        rev.setId("REV-002");
        rev.setPreguntaId("PREG-002");
        rev.setRevisorId("rev2");
        rev.setVeredicto(EstadoPregunta.RECHAZADA);
        rev.setObservaciones("Distractor C ambiguo");

        assertEquals("REV-002", rev.getId());
        assertEquals("PREG-002", rev.getPreguntaId());
        assertEquals("rev2", rev.getRevisorId());
        assertEquals(EstadoPregunta.RECHAZADA, rev.getVeredicto());
        assertEquals("Distractor C ambiguo", rev.getObservaciones());
    }

    @Test
    void toStringContieneInformacionClave() {
        Revision rev = new Revision("REV-100", "PREG-100", "rev1",
                EstadoPregunta.APROBADA, "Ok", LocalDateTime.now());
        String str = rev.toString();
        assertTrue(str.contains("REV-100"));
        assertTrue(str.contains("Aprobada") || str.contains("APROBADA"));
        assertTrue(str.contains("rev1"));
    }
}
