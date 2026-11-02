package co.unicauca.iso2.taller09.userservice;

import co.unicauca.iso2.taller09.common.dto.LoginRequest;
import co.unicauca.iso2.taller09.common.security.AuthTokenCodec;
import co.unicauca.iso2.taller09.common.security.AuthenticatedUser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:user-service-test;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthTokenCodec tokens;

    @Test
    void testLoginExitoso() throws Exception {
        LoginRequest req = new LoginRequest("autor1", "autor123");

        String body = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.user.username").value("autor1"))
                .andExpect(jsonPath("$.user.roles[0]").value("AUTOR"))
                .andReturn().getResponse().getContentAsString();

        JsonNode json = objectMapper.readTree(body);
        AuthenticatedUser identity = tokens.verify(json.get("token").asText()).orElseThrow();
        assertEquals(2L, identity.userId().longValue());
        assertEquals(List.of("AUTOR", "DOCENTE"), identity.roles());
    }

    @Test
    void testLoginFallidoCredencialesErroneas() throws Exception {
        LoginRequest req = new LoginRequest("autor1", "password_incorrecto");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.authenticated").value(false))
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testLoginIgnoraMayusculasEnElUsuario() throws Exception {
        LoginRequest req = new LoginRequest("ADMIN", "admin123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.roles[0]").value("ADMINISTRADOR"));
    }
}
