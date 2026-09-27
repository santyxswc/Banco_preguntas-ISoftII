/**
 * @file QuestionJdbcRepositoryH2Test.java
 * @brief Pruebas de integración del repositorio JDBC.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.access;

import co.unicauca.iso2.bancopreguntas.access.db.ConexionBD;
import co.unicauca.iso2.bancopreguntas.domain.EstadoPregunta;
import co.unicauca.iso2.bancopreguntas.domain.NivelDificultad;
import co.unicauca.iso2.bancopreguntas.domain.PaginaResultado;
import co.unicauca.iso2.bancopreguntas.domain.Question;
import co.unicauca.iso2.bancopreguntas.domain.QuestionBuilder;
import co.unicauca.iso2.bancopreguntas.domain.QuestionFilter;
import co.unicauca.iso2.bancopreguntas.domain.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @brief Pruebas de QuestionJdbcRepository sobre H2 en memoria con el
 *        esquema real de Flyway.
 *
 * Cada prueba usa una base con nombre aleatorio para quedar aislada.
 * Como preguntas.autor_id es llave foránea, setUp() inserta primero
 * los usuarios que usan las pruebas.
 */
class QuestionJdbcRepositoryH2Test {

    private QuestionRepository repository;

    @BeforeEach
    void setUp() throws SQLException {
        DataSource dataSource = ConexionBD.crearH2ParaTest(
                "test_" + UUID.randomUUID().toString().replace("-", ""));
        ConexionBD.migrar(dataSource);
        repository = new QuestionJdbcRepository(dataSource);

        sembrarUsuario(dataSource, "U-001");
        sembrarUsuario(dataSource, "U-002");
        sembrarUsuario(dataSource, "AUTOR-X");
        sembrarUsuario(dataSource, "AUTOR-Y");
    }

    /** @brief Inserta un usuario mínimo para cumplir la llave foránea. */
    private void sembrarUsuario(DataSource dataSource, String id) throws SQLException {
        String sql = "INSERT INTO usuarios (id, nombre, email, password, rol) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, "Usuario de prueba " + id);
            ps.setString(3, id.toLowerCase().replace("-", "") + "@test.unicauca.edu.co");
            ps.setString(4, "test123");
            ps.setString(5, "AUTOR");
            ps.executeUpdate();
        }
    }

    private Question preguntaValida(String id, String autorId, EstadoPregunta estado) {
        return QuestionBuilder.nueva()
                .conId(id)
                .conAutorId(autorId)
                .conNombre("Pregunta " + id)
                .conContexto("Contexto de más de veinte caracteres para " + id + ".")
                .conEnunciado("¿Cuál es la respuesta correcta para " + id + "?")
                .conOpcion("A", "Opción A de " + id)
                .conOpcion("B", "Opción B correcta de " + id)
                .conOpcion("C", "Opción C de " + id)
                .conOpcion("D", "Opción D de " + id)
                .conRespuestaCorrecta("B")
                .conJustificacion("Justificación de más de veinte caracteres para " + id + ".")
                .conBibliografia("Referencia 2026")
                .conCompetencia("Lectura crítica")
                .conTema("Comprensión textual")
                .conSubtema("Inferencia")
                .conNivelDificultad(NivelDificultad.INTERMEDIO)
                .conEstado(estado)
                .construir();
    }

    @Test
    void saveYFindByIdRecuperanLaPreguntaConSusOpciones() {

        Question original = preguntaValida("P-JDBC-1", "U-001", EstadoPregunta.BORRADOR);

        boolean guardada = repository.save(original);
        assertTrue(guardada);

        Question recuperada = repository.findById("P-JDBC-1");

        assertNotNull(recuperada);
        assertEquals("P-JDBC-1", recuperada.getId());
        assertEquals("U-001", recuperada.getAutorId());
        assertEquals(EstadoPregunta.BORRADOR, recuperada.getEstado());
        assertEquals(4, recuperada.getOpciones().size());
        assertEquals("B", recuperada.getRespuestaCorrecta());
    }

    @Test
    void findByIdDevuelveNullParaUnIdInexistente() {
        assertNull(repository.findById("NO-EXISTE"));
    }

    @Test
    void saveDevuelveFalsoSiElIdYaExiste() {

        repository.save(preguntaValida("P-JDBC-DUP", "U-001", EstadoPregunta.BORRADOR));
        boolean segundaVez = repository.save(
                preguntaValida("P-JDBC-DUP", "U-002", EstadoPregunta.BORRADOR));

        assertFalse(segundaVez);
    }

    @Test
    void updateModificaLosCamposYReemplazaLasOpciones() {

        repository.save(preguntaValida("P-JDBC-UPD", "U-001", EstadoPregunta.BORRADOR));

        Question modificada = repository.findById("P-JDBC-UPD");
        modificada.setNombre("Nombre actualizado");
        modificada.setEstado(EstadoPregunta.PENDIENTE_REVISION);

        boolean actualizada = repository.update(modificada);
        assertTrue(actualizada);

        Question recuperada = repository.findById("P-JDBC-UPD");
        assertEquals("Nombre actualizado", recuperada.getNombre());
        assertEquals(EstadoPregunta.PENDIENTE_REVISION, recuperada.getEstado());
        assertEquals(4, recuperada.getOpciones().size());
    }

    @Test
    void updateEstadoCambiaSoloElEstado() {

        repository.save(preguntaValida("P-JDBC-EST", "U-001", EstadoPregunta.BORRADOR));

        boolean actualizada = repository.updateEstado(
                "P-JDBC-EST", EstadoPregunta.ARCHIVADA);

        assertTrue(actualizada);
        assertEquals(EstadoPregunta.ARCHIVADA,
                repository.findById("P-JDBC-EST").getEstado());
    }

    @Test
    void findByAutorIdFiltraCorrectamente() {

        repository.save(preguntaValida("P-JDBC-A1", "AUTOR-X", EstadoPregunta.BORRADOR));
        repository.save(preguntaValida("P-JDBC-A2", "AUTOR-Y", EstadoPregunta.BORRADOR));

        List<Question> deAutorX = repository.findByAutorId("AUTOR-X");

        assertEquals(1, deAutorX.size());
        assertEquals("P-JDBC-A1", deAutorX.get(0).getId());
    }

    @Test
    void findByEstadoFiltraCorrectamente() {

        repository.save(preguntaValida("P-JDBC-E1", "U-001", EstadoPregunta.BORRADOR));
        repository.save(preguntaValida("P-JDBC-E2", "U-001", EstadoPregunta.PENDIENTE_REVISION));

        List<Question> pendientes = repository.findByEstado(
                EstadoPregunta.PENDIENTE_REVISION);

        assertEquals(1, pendientes.size());
        assertEquals("P-JDBC-E2", pendientes.get(0).getId());
    }

    @Test
    void buscarAplicaFiltroDeEstadoYPaginacionEnSQL() {

        for (int i = 1; i <= 5; i++) {
            repository.save(preguntaValida(
                    "P-JDBC-PAG-" + i, "U-001", EstadoPregunta.BORRADOR));
        }
        repository.save(preguntaValida(
                "P-JDBC-PAG-OTRO", "U-001", EstadoPregunta.PENDIENTE_REVISION));

        QuestionFilter filtro = new QuestionFilter()
                .conEstado(EstadoPregunta.BORRADOR)
                .conTamPagina(2)
                .conPagina(0);

        PaginaResultado<Question> pagina1 = repository.buscar(filtro);

        assertEquals(5, pagina1.getTotalElementos());
        assertEquals(2, pagina1.getElementos().size());
        assertEquals(3, pagina1.getTotalPaginas());
    }

    @Test
    void buscarConTextoLibreBuscaEnEnunciadoYContexto() {

        repository.save(preguntaValida("P-JDBC-TXT-1", "U-001", EstadoPregunta.BORRADOR));
        repository.save(preguntaValida("P-JDBC-TXT-2", "U-001", EstadoPregunta.BORRADOR));

        QuestionFilter filtro = new QuestionFilter()
                .conTextoLibre("P-JDBC-TXT-1")
                .conTamPagina(50);

        PaginaResultado<Question> resultado = repository.buscar(filtro);

        assertEquals(1, resultado.getTotalElementos());
        assertEquals("P-JDBC-TXT-1", resultado.getElementos().get(0).getId());
    }
}
