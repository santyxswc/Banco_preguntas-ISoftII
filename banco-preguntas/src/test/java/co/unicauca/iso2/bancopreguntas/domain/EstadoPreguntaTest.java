/**
 * @file EstadoPreguntaTest.java
 * @brief Pruebas de los estados de una pregunta.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.domain;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @brief Pruebas de EstadoPregunta.
 */
class EstadoPreguntaTest {

    @Test
    void soloBorradorPermiteEdicionYEnvioARevision() {
        for (EstadoPregunta estado : EstadoPregunta.values()) {
            boolean esBorrador = estado == EstadoPregunta.BORRADOR;
            assertEquals(esBorrador, estado.permiteEdicion());
            assertEquals(esBorrador, estado.permiteEnviarARevision());
        }
    }

    @Test
    void cadaEstadoTieneUnColorDistinto() {
        Set<java.awt.Color> colores = new HashSet<>();
        for (EstadoPregunta estado : EstadoPregunta.values()) {
            assertNotNull(estado.colorUI());
            colores.add(estado.colorUI());
        }
        assertEquals(EstadoPregunta.values().length, colores.size());
    }

    @Test
    void transicionesDelFlujoNormalSonValidas() {
        assertTrue(EstadoPregunta.BORRADOR.puedeCambiarA(EstadoPregunta.PENDIENTE_REVISION));
        assertTrue(EstadoPregunta.PENDIENTE_REVISION.puedeCambiarA(EstadoPregunta.EN_REVISION));
        assertTrue(EstadoPregunta.EN_REVISION.puedeCambiarA(EstadoPregunta.ARCHIVADA));
    }

    @Test
    void noSePuedeSaltarEstadosNiVolverDesdeArchivada() {
        assertFalse(EstadoPregunta.BORRADOR.puedeCambiarA(EstadoPregunta.EN_REVISION));
        assertFalse(EstadoPregunta.EN_REVISION.puedeCambiarA(EstadoPregunta.BORRADOR));
        assertTrue(EstadoPregunta.ARCHIVADA.siguientesPermitidos().isEmpty());
        assertFalse(EstadoPregunta.BORRADOR.puedeCambiarA(null));
    }

    @Test
    void toStringDevuelveLaEtiqueta() {
        assertEquals("Pendiente de revisión", EstadoPregunta.PENDIENTE_REVISION.toString());
        assertEquals("Borrador", EstadoPregunta.BORRADOR.getEtiqueta());
    }
}
