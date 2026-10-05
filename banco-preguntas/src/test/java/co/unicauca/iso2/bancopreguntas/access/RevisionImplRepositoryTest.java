/**
 * @file RevisionImplRepositoryTest.java
 * @brief Pruebas para RevisionImplRepository.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.access;

import co.unicauca.iso2.bancopreguntas.domain.EstadoPregunta;
import co.unicauca.iso2.bancopreguntas.domain.Revision;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RevisionImplRepositoryTest {

    private RevisionImplRepository repository;

    @BeforeEach
    void setUp() {
        repository = new RevisionImplRepository();
    }

    @Test
    void guardarRevisionAsignaIdSiEsNulo() {
        Revision rev = new Revision(null, "P1", "rev1", EstadoPregunta.APROBADA, "Bien", LocalDateTime.now());
        Revision guardada = repository.save(rev);

        assertNotNull(guardada);
        assertNotNull(guardada.getId());
        assertTrue(guardada.getId().startsWith("REV-"));
    }

    @Test
    void guardarRevisionNulaRetornaNull() {
        assertNull(repository.save(null));
    }

    @Test
    void findByPreguntaIdYFindByRevisorId() {
        Revision r1 = repository.save(new Revision(null, "P1", "rev1", EstadoPregunta.APROBADA, "Ok 1", LocalDateTime.now()));
        Revision r2 = repository.save(new Revision(null, "P1", "rev2", EstadoPregunta.RECHAZADA, "Ok 2", LocalDateTime.now()));
        Revision r3 = repository.save(new Revision(null, "P2", "rev1", EstadoPregunta.APROBADA, "Ok 3", LocalDateTime.now()));

        List<Revision> porPregunta = repository.findByPreguntaId("P1");
        assertEquals(2, porPregunta.size());

        List<Revision> porRevisor = repository.findByRevisorId("rev1");
        assertEquals(2, porRevisor.size());

        List<Revision> todas = repository.list();
        assertEquals(3, todas.size());
    }
}
