package co.unicauca.iso2.taller09.userservice;

import co.unicauca.iso2.taller09.common.constants.IdentityHeaders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:user-service-test;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listaLosUsuariosSembradosPorLaMigracion() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)))
                .andExpect(jsonPath("$[0].username").value("admin"));
    }

    @Test
    void filtraPorRol() throws Exception {
        mockMvc.perform(get("/users/role/revisor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(4))
                .andExpect(jsonPath("$[1].id").value(5));
    }

    @Test
    void rolDesconocidoDevuelve400() throws Exception {
        mockMvc.perform(get("/users/role/SUPERUSUARIO"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Rol desconocido: SUPERUSUARIO"));
    }

    @Test
    void revisoresElegiblesExcluyenAlAutorYALosYaAsignados() throws Exception {
        mockMvc.perform(get("/users/reviewers/eligible").param("authorId", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(5));

        mockMvc.perform(get("/users/reviewers/eligible").param("authorId", "2").param("excluding", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(4));
    }

    @Test
    void meUsaLaIdentidadPropagadaPorElGateway() throws Exception {
        mockMvc.perform(get("/users/me").header(IdentityHeaders.USER_ID, "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("autor2"));

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void laSincronizacionCreaElPerfilDeUnUsuarioNuevoDeKeycloak() throws Exception {
        mockMvc.perform(post("/internal/users/sync").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"Revisor3\",\"fullName\":\"Revisora Nueva\","
                                + "\"email\":\"revisor3@unicauca.edu.co\","
                                + "\"roles\":[\"REVISOR\",\"offline_access\",\"default-roles-banco-preguntas\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(greaterThanOrEqualTo(100)))
                .andExpect(jsonPath("$.username").value("revisor3"))
                .andExpect(jsonPath("$.roles", hasSize(1)))
                .andExpect(jsonPath("$.roles[0]").value("REVISOR"));

        mockMvc.perform(get("/users/role/revisor"))
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    @Transactional
    void laSincronizacionConservaElIdYAdoptaLosRolesDeKeycloak() throws Exception {
        mockMvc.perform(post("/internal/users/sync").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"autor2\",\"fullName\":\"Profesora María Gómez\","
                                + "\"email\":\"autor2@unicauca.edu.co\",\"roles\":[\"AUTOR\",\"REVISOR\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.fullName").value("Profesora María Gómez"))
                .andExpect(jsonPath("$.roles", hasSize(2)));
    }

    @Test
    void laSincronizacionSinUsuarioDevuelve400() throws Exception {
        mockMvc.perform(post("/internal/users/sync").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roles\":[\"AUTOR\"]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El nombre de usuario es obligatorio"));
    }
}
