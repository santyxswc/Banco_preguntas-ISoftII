/**
 * @file PreguntaRestControllerTest.java
 * @brief Pruebas de integración web para PreguntaRestController.
 * @author Santiago Caicedo
 * @author Ivan Alexander Lopez
 * @author Adrian Araujo Urbano
 * @author Carlos Bambague
 */
package co.unicauca.iso2.bancopreguntas.presentation.rest;

import co.unicauca.iso2.bancopreguntas.domain.EstadoPregunta;
import co.unicauca.iso2.bancopreguntas.domain.PreguntasDePrueba;
import co.unicauca.iso2.bancopreguntas.domain.Question;
import co.unicauca.iso2.bancopreguntas.presentation.rest.dto.DTOs.ApiResponse;
import co.unicauca.iso2.bancopreguntas.presentation.rest.dto.DTOs.CambioEstadoRequest;
import co.unicauca.iso2.bancopreguntas.presentation.rest.dto.PreguntaDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class PreguntaRestControllerTest {

    @Autowired
    private PreguntaRestController controller;

    @Test
    void crearPreguntaValidaRetorna201() {
        Question q = PreguntasDePrueba.preguntaValida("autor1");
        PreguntaDTO dto = PreguntaDTO.fromEntity(q);

        ResponseEntity<ApiResponse<PreguntaDTO>> response = controller.crearPregunta(dto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isExito());
        assertNotNull(response.getBody().getDatos().getId());
    }

    @Test
    void validarPreguntaInvalidaRetorna400() {
        PreguntaDTO dto = new PreguntaDTO(); // vacía

        ResponseEntity<ApiResponse<List<String>>> response = controller.validarPregunta(dto);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(!response.getBody().getErrores().isEmpty());
    }

    @Test
    void cambiarEstadoTransicionValida() {
        Question q = PreguntasDePrueba.preguntaValida("autor1");
        PreguntaDTO dto = PreguntaDTO.fromEntity(q);
        ResponseEntity<ApiResponse<PreguntaDTO>> created = controller.crearPregunta(dto);
        String id = created.getBody().getDatos().getId();

        CambioEstadoRequest req = new CambioEstadoRequest();
        req.setNuevoEstado(EstadoPregunta.PENDIENTE_REVISION);

        ResponseEntity<ApiResponse<PreguntaDTO>> response = controller.cambiarEstado(id, req);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(EstadoPregunta.PENDIENTE_REVISION, response.getBody().getDatos().getEstado());
    }
}
