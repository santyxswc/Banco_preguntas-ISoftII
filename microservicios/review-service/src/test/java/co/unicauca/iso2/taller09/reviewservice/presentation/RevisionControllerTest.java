package co.unicauca.iso2.taller09.reviewservice.presentation;

import co.unicauca.iso2.taller09.common.enums.AssignmentStatus;
import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import co.unicauca.iso2.taller09.common.enums.ReviewResult;
import co.unicauca.iso2.taller09.reviewservice.application.AssignedQuestionView;
import co.unicauca.iso2.taller09.reviewservice.application.EvaluationOutcome;
import co.unicauca.iso2.taller09.reviewservice.application.ReviewService;
import co.unicauca.iso2.taller09.reviewservice.domain.exception.ReviewException;
import co.unicauca.iso2.taller09.reviewservice.domain.model.PageResult;
import co.unicauca.iso2.taller09.reviewservice.domain.model.Review;
import co.unicauca.iso2.taller09.reviewservice.domain.model.ReviewAssignment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {RevisionController.class, AsignacionController.class})
class RevisionControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ReviewService reviewService;

    @Test
    void asignarDevuelve201() throws Exception {
        when(reviewService.assignReviewers(eq(100L), anyList(), eq(1L)))
                .thenReturn(List.of(new ReviewAssignment(1L, 100L, 4L, 1L, 1, LocalDateTime.now(), AssignmentStatus.ASSIGNED)));

        mvc.perform(post("/api/v1/asignaciones").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"questionId\":100,\"reviewerIds\":[4],\"assignedBy\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].reviewerId").value(4));
    }

    @Test
    void asignarConMasDeTresDevuelve400() throws Exception {
        when(reviewService.assignReviewers(any(), anyList(), any()))
                .thenThrow(ReviewException.validation("Debe asignar entre 1 y 3 revisores."));

        mvc.perform(post("/api/v1/asignaciones").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"questionId\":100,\"reviewerIds\":[3,4,5,9],\"assignedBy\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Debe asignar entre 1 y 3 revisores."));
    }

    @Test
    void listarAsignadasPaginado() throws Exception {
        AssignedQuestionView view = new AssignedQuestionView(1L, 100L, "Pregunta", 2L, 1, LocalDateTime.now(), AssignmentStatus.ASSIGNED);
        when(reviewService.listAssigned(4L, 0, 5)).thenReturn(new PageResult<>(List.of(view), 0, 5, 1));

        mvc.perform(get("/api/v1/revisiones/asignadas").param("revisorId", "4").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].questionId").value(100))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void evaluarDevuelveMensajeYColor() throws Exception {
        Review review = new Review(7L, 100L, 4L, 1, ReviewResult.APPROVED, null, LocalDateTime.now());
        when(reviewService.evaluate(100L, 4L, ReviewResult.APPROVED, null))
                .thenReturn(new EvaluationOutcome(review, QuestionStatus.APPROVED));

        mvc.perform(post("/api/v1/revisiones/evaluar").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"questionId\":100,\"reviewerId\":4,\"result\":\"APPROVED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Evaluación registrada correctamente"))
                .andExpect(jsonPath("$.questionFinalState").value("APPROVED"))
                .andExpect(jsonPath("$.stateColor").value("#28A745"));
    }

    @Test
    void evaluarSinAsignacionDevuelve403() throws Exception {
        when(reviewService.evaluate(100L, 5L, ReviewResult.APPROVED, null))
                .thenThrow(ReviewException.forbidden(ReviewService.MENSAJE_NO_ASIGNADA));

        mvc.perform(post("/api/v1/revisiones/evaluar").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"questionId\":100,\"reviewerId\":5,\"result\":\"APPROVED\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("No tiene asignada esta pregunta"));
    }

    @Test
    void servicioDependienteCaidoDevuelve503() throws Exception {
        when(reviewService.observations(100L)).thenThrow(ReviewException.unavailable("El servicio de preguntas no está disponible."));

        mvc.perform(get("/api/v1/revisiones/pregunta/100"))
                .andExpect(status().isServiceUnavailable());
    }
}
