/**
 * @file PreguntaAggregateTest.java
 * @brief Pruebas unitarias de invariantes del Agregado Pregunta (Taller 11 - DDD).
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @brief Suite de pruebas para verificar que el Aggregate Root Pregunta proteja todas sus invariantes.
 */
class PreguntaAggregateTest {

    private Pregunta pregunta;

    @BeforeEach
    void setUp() {
        pregunta = new Pregunta(
                1L,
                "docente-saberpro-1",
                new Enunciado("¿Cuál es el principio SOLID que promueve la inversión de dependencias?"),
                new Justificacion("El principio DIP establece que los módulos de alto nivel no deben depender de los de bajo nivel.")
        );
    }

    @Test
    @DisplayName("Invariante: no debe permitir enviar a revisión sin tener 4 opciones")
    void noDebePermitirEnviarARevisionSinCuatroOpciones() {
        pregunta.agregarOpcion("Opción A", true);
        assertThrows(ReglaNegocioException.class, pregunta::enviarARevision);
    }

    @Test
    @DisplayName("Invariante: no debe permitir más de 4 opciones")
    void noDebePermitirMasDeCuatroOpciones() {
        pregunta.agregarOpcion("Opción A", true);
        pregunta.agregarOpcion("Opción B", false);
        pregunta.agregarOpcion("Opción C", false);
        pregunta.agregarOpcion("Opción D", false);

        assertThrows(ReglaNegocioException.class, () ->
                pregunta.agregarOpcion("Opción E", false));
    }

    @Test
    @DisplayName("Invariante: no debe permitir enviar a revisión sin exactamente 1 opción correcta (ninguna correcta)")
    void noDebePermitirEnviarARevisionSinOpcionCorrecta() {
        pregunta.agregarOpcion("Opción A", false);
        pregunta.agregarOpcion("Opción B", false);
        pregunta.agregarOpcion("Opción C", false);
        pregunta.agregarOpcion("Opción D", false);

        assertThrows(ReglaNegocioException.class, pregunta::enviarARevision);
    }

    @Test
    @DisplayName("Invariante: no debe permitir enviar a revisión con múltiples opciones correctas")
    void noDebePermitirEnviarARevisionConMultiplesOpcionesCorrectas() {
        pregunta.agregarOpcion("Opción A", true);
        pregunta.agregarOpcion("Opción B", true);
        pregunta.agregarOpcion("Opción C", false);
        pregunta.agregarOpcion("Opción D", false);

        assertThrows(ReglaNegocioException.class, pregunta::enviarARevision);
    }

    @Test
    @DisplayName("Invariante: no debe permitir enviar a revisión sin enunciado o sin justificación")
    void noDebePermitirEnviarARevisionSinEnunciadoOJustificacion() {
        Pregunta incompleta = new Pregunta();
        incompleta.agregarOpcion("Opción A", true);
        incompleta.agregarOpcion("Opción B", false);
        incompleta.agregarOpcion("Opción C", false);
        incompleta.agregarOpcion("Opción D", false);

        assertThrows(ReglaNegocioException.class, incompleta::enviarARevision);
    }

    @Test
    @DisplayName("Éxito: debe transicionar a PENDIENTE_REVISION cuando cumple todas las invariantes")
    void debeTransicionarAPendienteRevisionConExito() {
        pregunta.agregarOpcion("Dependency Inversion Principle (DIP)", true);
        pregunta.agregarOpcion("Single Responsibility Principle (SRP)", false);
        pregunta.agregarOpcion("Open / Closed Principle (OCP)", false);
        pregunta.agregarOpcion("Liskov Substitution Principle (LSP)", false);

        pregunta.enviarARevision();

        assertEquals(EstadoPregunta.PENDIENTE_REVISION, pregunta.getEstado());
        assertEquals(4, pregunta.getOpciones().size());
    }

    @Test
    @DisplayName("Invariante: no debe permitir modificar opciones una vez enviada a revisión")
    void noDebePermitirModificarOpcionesFueraDeBorrador() {
        pregunta.agregarOpcion("Opción A", true);
        pregunta.agregarOpcion("Opción B", false);
        pregunta.agregarOpcion("Opción C", false);
        pregunta.agregarOpcion("Opción D", false);
        pregunta.enviarARevision();

        assertThrows(ReglaNegocioException.class, () ->
                pregunta.agregarOpcion("Intento de cambio", false));
    }

    @Test
    @DisplayName("Ciclo de vida completo: Borrador -> Pendiente -> En Revisión -> Aprobada")
    void flujoCompletoDeAprobacion() {
        pregunta.agregarOpcion("Opción A", true);
        pregunta.agregarOpcion("Opción B", false);
        pregunta.agregarOpcion("Opción C", false);
        pregunta.agregarOpcion("Opción D", false);

        pregunta.enviarARevision();
        assertEquals(EstadoPregunta.PENDIENTE_REVISION, pregunta.getEstado());

        pregunta.iniciarRevision();
        assertEquals(EstadoPregunta.EN_REVISION, pregunta.getEstado());

        pregunta.aprobar();
        assertEquals(EstadoPregunta.APROBADA, pregunta.getEstado());
    }

    @Test
    @DisplayName("Ciclo de vida de rechazo: Borrador -> Pendiente -> En Revisión -> Rechazada con motivo")
    void flujoDeRechazoConMotivo() {
        pregunta.agregarOpcion("Opción A", true);
        pregunta.agregarOpcion("Opción B", false);
        pregunta.agregarOpcion("Opción C", false);
        pregunta.agregarOpcion("Opción D", false);

        pregunta.enviarARevision();
        pregunta.iniciarRevision();

        assertThrows(ReglaNegocioException.class, () -> pregunta.rechazar("   "));

        pregunta.rechazar("El enunciado presenta ambigüedad en los términos pedagógicos.");
        assertEquals(EstadoPregunta.RECHAZADA, pregunta.getEstado());
    }
}
