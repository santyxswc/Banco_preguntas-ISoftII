/**
 * @file EventBusTest.java
 * @brief Pruebas unitarias para el patrón GoF Singleton (EventBus).
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.infra.events;

import co.unicauca.iso2.bancopreguntas.domain.EstadoPregunta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class EventBusTest {

    @BeforeEach
    void setUp() {
        EventBus.resetInstance();
    }

    @Test
    void getInstanceDevuelveSiempreLaMismaInstancia() {
        EventBus instance1 = EventBus.getInstance();
        EventBus instance2 = EventBus.getInstance();

        assertNotNull(instance1);
        assertSame(instance1, instance2, "EventBus debe ser un Singleton estricto");
    }

    @Test
    void publicacionYSuscripcionDeEventosMedianteBus() {
        EventBus bus = EventBus.getInstance();
        List<RevisionCompletadaEvent> revisionEvents = new ArrayList<>();
        List<RevisorAsignadoEvent> asignacionEvents = new ArrayList<>();

        bus.subscribeRevision(revisionEvents::add);
        bus.subscribe(asignacionEvents::add);

        bus.publish(new RevisorAsignadoEvent("P1", List.of("rev1"), LocalDateTime.now()));
        bus.publishRevision(new RevisionCompletadaEvent("P1", "rev1", EstadoPregunta.APROBADA, "Ok", LocalDateTime.now()));

        assertEquals(1, asignacionEvents.size());
        assertEquals("P1", asignacionEvents.get(0).getPreguntaId());

        assertEquals(1, revisionEvents.size());
        assertEquals(EstadoPregunta.APROBADA, revisionEvents.get(0).getVeredicto());
    }
}
