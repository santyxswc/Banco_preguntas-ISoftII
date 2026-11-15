package co.unicauca.iso2.taller09.userservice.domain;

import co.unicauca.iso2.taller09.common.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas unitarias de la entidad {@link User}: reglas de revisión y sincronización con Keycloak. */
class UserDomainTest {

    @Test
    @DisplayName("Un revisor no puede revisar su propia pregunta")
    void unRevisorNoPuedeRevisarSuPropiaPregunta() {
        User revisor = new User(4L, "revisor1", "Revisor", "r@u.co", List.of(Role.REVISOR, Role.DOCENTE));
        User autor = new User(2L, "autor1", "Autor", "a@u.co", List.of(Role.AUTOR));

        assertTrue(revisor.canReview(2L));
        assertTrue(revisor.canReview(null));
        assertFalse(revisor.canReview(4L));
        assertFalse(autor.canReview(9L));
        assertEquals(List.of("REVISOR", "DOCENTE"), revisor.roleNames());
        assertTrue(revisor.hasRole(Role.DOCENTE));
        assertFalse(revisor.hasRole(null));
    }

    @Test
    @DisplayName("El perfil creado desde la identidad normaliza el usuario y completa nombre y correo")
    void perfilDesdeLaIdentidad() {
        User nuevo = User.fromIdentity("  Revisor3 ", "Revisora Nueva", null, List.of(Role.REVISOR));

        assertNull(nuevo.getId());
        assertEquals("revisor3", nuevo.getUsername());
        assertEquals("Revisora Nueva", nuevo.getFullName());
        assertEquals("revisor3@unicauca.edu.co", nuevo.getEmail());
        assertEquals(Set.of(Role.REVISOR), nuevo.getRoles());

        assertEquals("autor9", User.fromIdentity("autor9", " ", "a9@u.co", null).getFullName());
        assertThrows(IllegalArgumentException.class, () -> User.fromIdentity(" ", "x", "y", List.of()));
    }

    @Test
    @DisplayName("La sincronización adopta los roles de Keycloak y dice si hubo cambios")
    void sincronizacion() {
        User usuario = new User(7L, "docente7", "Docente Siete", "d7@u.co", List.of(Role.AUTOR));

        assertFalse(usuario.synchronizeWith("Docente Siete", "d7@u.co", List.of(Role.AUTOR)));
        assertTrue(usuario.synchronizeWith("Docente Siete", "d7@u.co", List.of(Role.AUTOR, Role.REVISOR)));
        assertEquals(Set.of(Role.AUTOR, Role.REVISOR), usuario.getRoles());

        assertTrue(usuario.synchronizeWith("Nombre Nuevo", null, List.of(Role.AUTOR, Role.REVISOR)));
        assertEquals("Nombre Nuevo", usuario.getFullName());
        assertEquals("d7@u.co", usuario.getEmail());

        assertTrue(usuario.synchronizeWith(null, null, null));
        assertTrue(usuario.getRoles().isEmpty());
        assertEquals(7L, usuario.toDTO().getId());
    }
}
