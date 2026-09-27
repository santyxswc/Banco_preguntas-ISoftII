/**
 * @file EntidadesDominioTest.java
 * @brief Pruebas de las entidades de apoyo del dominio.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @brief Pruebas de Usuario, Asignacion, catálogo, PaginaResultado y
 *        QuestionFilter.
 */
class EntidadesDominioTest {

    @Test
    void usuarioSeMuestraConSuRol() {
        Usuario u = new Usuario("U-1", "Ana", "ana@unicauca.edu.co", "123", Rol.AUTOR);
        assertEquals("Ana (Autor)", u.toString());
        u.setRol(null);
        assertEquals("Ana (-)", u.toString());
    }

    @Test
    void asignacionCopiaLaListaDeRevisores() {
        List<String> revisores = new ArrayList<>(List.of("R-1"));
        Asignacion a = new Asignacion("A-1", "P-1", revisores, LocalDateTime.now());

        revisores.add("R-2");

        assertEquals(List.of("R-1"), a.getRevisorIds());
        a.setRevisorIds(null);
        assertTrue(a.getRevisorIds().isEmpty());
    }

    @Test
    void entidadesDeCatalogoSeComparanPorId() {
        assertEquals(new Competencia("C-1", "Lectura"), new Competencia("C-1", "Otro"));
        assertNotEquals(new Competencia("C-1", "Lectura"), new Competencia("C-2", "Lectura"));
        assertEquals(new Tema("T-1", "A", "C-1"), new Tema("T-1", "B", "C-2"));
        assertEquals(new Subtema("S-1", "A", "T-1").hashCode(),
                new Subtema("S-1", "B", "T-2").hashCode());
        assertEquals("Lectura", new Competencia("C-1", "Lectura").toString());
    }

    @Test
    void paginaResultadoCalculaElTotalDePaginas() {
        PaginaResultado<String> p = new PaginaResultado<>(List.of("a", "b"), 5, 0, 2);
        assertEquals(3, p.getTotalPaginas());

        PaginaResultado<String> vacia = new PaginaResultado<>(null, 0, -1, 0);
        assertEquals(1, vacia.getTotalPaginas());
        assertEquals(0, vacia.getPagina());
        assertTrue(vacia.getElementos().isEmpty());
    }

    @Test
    void questionFilterCorrigeValoresInvalidos() {
        QuestionFilter f = new QuestionFilter()
                .conPagina(-3)
                .conTamPagina(0)
                .conOrden(null);

        assertEquals(0, f.getPagina());
        assertEquals(10, f.getTamPagina());
        assertEquals(QuestionFilter.Orden.FECHA_CREACION_DESC, f.getOrden());
    }
}
