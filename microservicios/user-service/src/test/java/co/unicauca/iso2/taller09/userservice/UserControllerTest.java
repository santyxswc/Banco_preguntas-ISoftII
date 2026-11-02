package co.unicauca.iso2.taller09.userservice;

import co.unicauca.iso2.taller09.common.constants.IdentityHeaders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}
