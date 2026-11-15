package co.unicauca.iso2.taller09.questionservice.security;

import co.unicauca.iso2.taller09.common.enums.Role;
import co.unicauca.iso2.taller09.common.security.RequestIdentity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas unitarias de la autorización por dato que aplican los servicios con la identidad del gateway. */
class RequestIdentityTest {

    @Test
    @DisplayName("Lee los roles de la cabecera separada por comas, sin distinguir mayúsculas")
    void leeLosRolesDeLaCabecera() {
        RequestIdentity autor = RequestIdentity.of(2L, " AUTOR , docente ,");

        assertEquals(List.of("AUTOR", "docente"), autor.roles());
        assertTrue(autor.hasRole(Role.AUTOR));
        assertTrue(autor.hasRole(Role.DOCENTE));
        assertFalse(autor.hasRole(Role.ADMINISTRADOR));
        assertFalse(autor.hasRole(null));
        assertFalse(autor.isAnonymous());
        assertTrue(RequestIdentity.of(2L, null).roles().isEmpty());
        assertTrue(new RequestIdentity(2L, null).roles().isEmpty());
    }

    @Test
    @DisplayName("Un autor solo lista sus preguntas aunque pida las de otro autor")
    void elAutorSoloVeLoPropio() {
        RequestIdentity autor = RequestIdentity.of(2L, "AUTOR,DOCENTE");

        assertEquals(2L, autor.ownerFilter(3L));
        assertEquals(2L, autor.ownerFilter(null));
        assertTrue(autor.canAccessDataOf(2L));
        assertFalse(autor.canAccessDataOf(3L));
    }

    @Test
    @DisplayName("El administrador conserva el filtro que pidió y accede a los datos de cualquiera")
    void elAdministradorVeTodo() {
        RequestIdentity admin = RequestIdentity.of(1L, "ADMINISTRADOR");

        assertTrue(admin.isAdministrator());
        assertEquals(3L, admin.ownerFilter(3L));
        assertNull(admin.ownerFilter(null));
        assertTrue(admin.canAccessDataOf(3L));
    }

    @Test
    @DisplayName("Una llamada interna entre servicios no trae identidad y no se restringe")
    void llamadaInternaSinIdentidad() {
        RequestIdentity interna = RequestIdentity.of(null, null);

        assertTrue(interna.isAnonymous());
        assertEquals(3L, interna.ownerFilter(3L));
        assertTrue(interna.canAccessDataOf(3L));
    }
}
