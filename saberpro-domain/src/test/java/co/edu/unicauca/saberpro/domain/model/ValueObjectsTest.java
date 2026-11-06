/**
 * @file ValueObjectsTest.java
 * @brief Pruebas unitarias de los Objetos de Valor (Value Objects) del dominio Saber Pro (DDD).
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 */
package co.edu.unicauca.saberpro.domain.model;

import co.edu.unicauca.saberpro.domain.exception.ReglaNegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @brief Pruebas de inmutabilidad, validación e igualdad estructural de los Value Objects.
 */
class ValueObjectsTest {

    @Test
    @DisplayName("Justificación: falla si es nula o menor a 15 caracteres")
    void justificacionValidaLongitudMinima() {
        assertThrows(ReglaNegocioException.class, () -> new Justificacion(null));
        assertThrows(ReglaNegocioException.class, () -> new Justificacion("Corta"));
        assertThrows(ReglaNegocioException.class, () -> new Justificacion("   1234567890   ")); // 10 chars tras trim

        Justificacion justificacion = new Justificacion("Esta justificación cumple con los 15 caracteres mínimos requeridos.");
        assertEquals("Esta justificación cumple con los 15 caracteres mínimos requeridos.", justificacion.getTexto());
    }

    @Test
    @DisplayName("Justificación: rechaza palabras prohibidas sin importar mayúsculas ni tildes")
    void justificacionRechazaPalabrasProhibidas() {
        assertThrows(ReglaNegocioException.class,
                () -> new Justificacion("Obviamente la opción B es la respuesta correcta."));
        assertThrows(ReglaNegocioException.class,
                () -> new Justificacion("Es la clave porque SÍ cumple lo que pide el enunciado."));
        assertThrows(ReglaNegocioException.class,
                () -> new Justificacion("La respuesta es todas las anteriores sin más detalle."));

        // "claramente" prohibida, pero "aclaramos" no debe confundirse con ella (límite de palabra).
        Justificacion valida = new Justificacion("Aclaramos que la opción B aplica el patrón Observer al evento.");
        assertTrue(valida.getTexto().startsWith("Aclaramos"));
    }

    @Test
    @DisplayName("Justificación: igualdad por valor (equals y hashCode)")
    void justificacionIgualdadPorValor() {
        Justificacion j1 = new Justificacion("Texto de prueba idéntico para justificación.");
        Justificacion j2 = new Justificacion("Texto de prueba idéntico para justificación.");
        Justificacion j3 = new Justificacion("Texto alternativo y diferente de justificación.");

        assertEquals(j1, j2);
        assertEquals(j1.hashCode(), j2.hashCode());
        assertNotEquals(j1, j3);
    }

    @Test
    @DisplayName("Enunciado: valida longitud mínima y no nulos")
    void enunciadoValidaLongitud() {
        assertThrows(ReglaNegocioException.class, () -> new Enunciado(null));
        assertThrows(ReglaNegocioException.class, () -> new Enunciado("Corto"));

        Enunciado e = new Enunciado("¿Cuál es el patrón creacional utilizado?");
        assertEquals("¿Cuál es el patrón creacional utilizado?", e.getTexto());
    }

    @Test
    @DisplayName("Enunciado: igualdad por valor")
    void enunciadoIgualdadPorValor() {
        Enunciado e1 = new Enunciado("¿Pregunta número 1 para el examen Saber Pro?");
        Enunciado e2 = new Enunciado("¿Pregunta número 1 para el examen Saber Pro?");

        assertEquals(e1, e2);
        assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    @DisplayName("OpcionRespuesta: valida texto y almacena condición de clave")
    void opcionRespuestaComportamiento() {
        assertThrows(ReglaNegocioException.class, () -> new OpcionRespuesta(null, true));
        assertThrows(ReglaNegocioException.class, () -> new OpcionRespuesta("   ", false));

        OpcionRespuesta correcta = new OpcionRespuesta("Opción correcta de análisis", true);
        assertTrue(correcta.isEsCorrecta());
        assertEquals("Opción correcta de análisis", correcta.getTexto());

        OpcionRespuesta otra = new OpcionRespuesta("Opción correcta de análisis", true);
        assertEquals(correcta, otra);
        assertEquals(correcta.hashCode(), otra.hashCode());
    }
}
