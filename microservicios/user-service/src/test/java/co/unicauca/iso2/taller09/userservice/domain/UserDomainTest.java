package co.unicauca.iso2.taller09.userservice.domain;

import co.unicauca.iso2.taller09.common.enums.Role;
import co.unicauca.iso2.taller09.common.security.AuthTokenCodec;
import co.unicauca.iso2.taller09.common.security.AuthenticatedUser;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserDomainTest {

    private static final String SECRET = "secreto-de-pruebas-1234";

    @Test
    void elHashUsaSalAleatoriaYVerificaLaContrasena() {
        String primero = PasswordHasher.hash("autor123");
        String segundo = PasswordHasher.hash("autor123");

        assertNotEquals(primero, segundo);
        assertTrue(PasswordHasher.matches("autor123", primero));
        assertFalse(PasswordHasher.matches("otra", primero));
        assertFalse(PasswordHasher.matches("autor123", "sin-separador"));
    }

    @Test
    void unRevisorNoPuedeRevisarSuPropiaPregunta() {
        User revisor = new User(4L, "revisor1", "x", "Revisor", "r@u.co", List.of(Role.REVISOR, Role.DOCENTE));
        User autor = new User(2L, "autor1", "x", "Autor", "a@u.co", List.of(Role.AUTOR));

        assertTrue(revisor.canReview(2L));
        assertFalse(revisor.canReview(4L));
        assertFalse(autor.canReview(9L));
        assertEquals(List.of("REVISOR", "DOCENTE"), revisor.roleNames());
    }

    @Test
    void elTokenFirmadoSeVerificaYDetectaAlteracionesYVencimiento() {
        Instant ahora = Instant.parse("2026-11-02T12:00:00Z");
        AuthTokenCodec codec = new AuthTokenCodec(SECRET, Duration.ofMinutes(30), Clock.fixed(ahora, ZoneOffset.UTC));
        String token = codec.issue(5L, "revisor2", List.of("REVISOR", "DOCENTE"));

        AuthenticatedUser usuario = codec.verify(token).orElseThrow();
        assertEquals(5L, usuario.userId().longValue());
        assertEquals("revisor2", usuario.username());
        assertTrue(usuario.hasRole("revisor"));

        String alterado = (token.charAt(0) == 'a' ? "b" : "a") + token.substring(1);
        assertTrue(codec.verify(alterado).isEmpty());
        assertTrue(new AuthTokenCodec("otro-secreto-distinto", Duration.ofMinutes(30)).verify(token).isEmpty());

        AuthTokenCodec despues = new AuthTokenCodec(SECRET, Duration.ofMinutes(30),
                Clock.fixed(ahora.plus(Duration.ofMinutes(31)), ZoneOffset.UTC));
        assertTrue(despues.verify(token).isEmpty());
        assertTrue(codec.verify("basura").isEmpty());
    }
}
