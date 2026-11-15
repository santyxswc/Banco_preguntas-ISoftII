package co.unicauca.iso2.taller09.userservice.repository;

import co.unicauca.iso2.taller09.common.enums.Role;
import co.unicauca.iso2.taller09.userservice.domain.User;
import co.unicauca.iso2.taller09.userservice.repository.jpa.JpaUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import(JpaUserRepository.class)
class JpaUserRepositoryTest {

    @Autowired
    private JpaUserRepository repository;

    @Test
    void laMigracionSiembraLosUsuariosConSusRolesYSinContrasenas() {
        User revisor = repository.findByUsername("REVISOR1").orElseThrow();

        assertEquals(4L, revisor.getId().longValue());
        assertEquals(Set.of(Role.REVISOR, Role.DOCENTE), revisor.getRoles());
        assertEquals("revisor1@unicauca.edu.co", revisor.getEmail());
    }

    @Test
    void buscaPorRolOrdenadoPorId() {
        List<Long> ids = repository.findByRole(Role.AUTOR).stream().map(User::getId).toList();

        assertEquals(List.of(2L, 3L), ids);
    }

    @Test
    void guardaUnUsuarioNuevoConIdGeneradoYRoles() {
        User nuevo = new User(null, "revisor3", "Revisora Nueva",
                "revisor3@unicauca.edu.co", List.of(Role.REVISOR));

        User guardado = repository.save(nuevo);

        assertTrue(guardado.getId() >= 100L);
        User leido = repository.findById(guardado.getId()).orElseThrow();
        assertEquals(Set.of(Role.REVISOR), leido.getRoles());
        assertEquals(3, repository.findByRole(Role.REVISOR).size());
    }
}
