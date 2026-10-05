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
    void borradorYRechazadaPermitenEdicionYReenvio() {
        for (EstadoPregunta estado : EstadoPregunta.values()) {
            boolean permiteEdicionYReenvio = (estado == EstadoPregunta.BORRADOR || estado == EstadoPregunta.RECHAZADA);
            assertEquals(permiteEdicionYReenvio, estado.permiteEdicion(),
                    "Estado " + estado + " falló en permiteEdicion");
            assertEquals(permiteEdicionYReenvio, estado.permiteEnviarARevision(),
                    "Estado " + estado + " falló en permiteEnviarARevision");
        }
    }

    @Test
    void cadaEstadoTieneUnColorDistintoYHexadecimalValido() {
        Set<java.awt.Color> colores = new HashSet<>();
        Set<String> coloresHex = new HashSet<>();
        for (EstadoPregunta estado : EstadoPregunta.values()) {
            assertNotNull(estado.colorUI());
            assertNotNull(estado.colorHex());
            assertTrue(estado.colorHex().matches("^#[0-9A-F]{6}$"), "Formato hex inválido para " + estado);
            colores.add(estado.colorUI());
            coloresHex.add(estado.colorHex());
        }
        assertEquals(EstadoPregunta.values().length, colores.size());
        assertEquals(EstadoPregunta.values().length, coloresHex.size());
    }

    @Test
    void transicionesDelFlujoNormalSonValidas() {
        assertTrue(EstadoPregunta.BORRADOR.puedeCambiarA(EstadoPregunta.PENDIENTE_REVISION));
        assertTrue(EstadoPregunta.PENDIENTE_REVISION.puedeCambiarA(EstadoPregunta.EN_REVISION));
        assertTrue(EstadoPregunta.EN_REVISION.puedeCambiarA(EstadoPregunta.APROBADA));
        assertTrue(EstadoPregunta.EN_REVISION.puedeCambiarA(EstadoPregunta.RECHAZADA));
        assertTrue(EstadoPregunta.APROBADA.puedeCambiarA(EstadoPregunta.PUBLICADA));
        assertTrue(EstadoPregunta.RECHAZADA.puedeCambiarA(EstadoPregunta.PENDIENTE_REVISION));
        assertTrue(EstadoPregunta.PUBLICADA.puedeCambiarA(EstadoPregunta.ARCHIVADA));
    }

    @Test
    void noSePuedeSaltarEstadosNiVolverDesdeArchivada() {
        assertFalse(EstadoPregunta.BORRADOR.puedeCambiarA(EstadoPregunta.EN_REVISION));
        assertFalse(EstadoPregunta.BORRADOR.puedeCambiarA(EstadoPregunta.APROBADA));
        assertFalse(EstadoPregunta.EN_REVISION.puedeCambiarA(EstadoPregunta.BORRADOR));
        assertTrue(EstadoPregunta.ARCHIVADA.siguientesPermitidos().isEmpty());
        assertFalse(EstadoPregunta.BORRADOR.puedeCambiarA(null));
    }

    @Test
    void toStringDevuelveLaEtiqueta() {
        assertEquals("Pendiente de revisión", EstadoPregunta.PENDIENTE_REVISION.toString());
        assertEquals("Borrador", EstadoPregunta.BORRADOR.getEtiqueta());
        assertEquals("Aprobada", EstadoPregunta.APROBADA.getEtiqueta());
        assertEquals("Rechazada", EstadoPregunta.RECHAZADA.getEtiqueta());
        assertEquals("Publicada", EstadoPregunta.PUBLICADA.getEtiqueta());
    }
}
