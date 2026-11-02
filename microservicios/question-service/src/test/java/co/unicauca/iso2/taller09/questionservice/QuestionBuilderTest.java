package co.unicauca.iso2.taller09.questionservice;

import co.unicauca.iso2.taller09.common.dto.CreateQuestionRequest;
import co.unicauca.iso2.taller09.common.dto.OptionDTO;
import co.unicauca.iso2.taller09.common.dto.QuestionDTO;
import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import co.unicauca.iso2.taller09.questionservice.domain.Question;
import co.unicauca.iso2.taller09.questionservice.domain.QuestionBuilder;
import co.unicauca.iso2.taller09.questionservice.domain.QuestionFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias de la entidad Question, su Builder y el filtro de búsqueda. */
class QuestionBuilderTest {

    static CreateQuestionRequest solicitud(Long autor) {
        return new CreateQuestionRequest(
                "  Contexto de la pregunta con suficiente longitud para el DCE.  ",
                "¿Qué patrón separa la construcción de un objeto complejo de su representación?",
                List.of(new OptionDTO("A", "Builder"), new OptionDTO("B", "Observer"),
                        new OptionDTO("C", "Facade"), new OptionDTO("D", "Adapter")),
                "A", "Builder construye el objeto paso a paso.", "Gamma et al., 1994",
                "Diseño de software", "Patrones", "Creacionales", "medio", autor);
    }

    @Test
    @DisplayName("El builder copia todos los campos de la solicitud y normaliza textos")
    void desdeSolicitud() {
        Question q = QuestionBuilder.from(solicitud(2L)).build();

        assertEquals("Contexto de la pregunta con suficiente longitud para el DCE.", q.getContext());
        assertEquals("MEDIO", q.getDifficulty());
        assertEquals(4, q.getOptions().size());
        assertEquals("A", q.getCorrectOptionId());
        assertEquals(2L, q.getAuthorId());
        assertEquals(QuestionStatus.DRAFT, q.getStatus());
    }

    @Test
    @DisplayName("El builder exige autor y enunciado")
    void invariantes() {
        assertThrows(NullPointerException.class, () -> QuestionBuilder.aQuestion().statement("¿Algo?").build());
        assertThrows(IllegalStateException.class, () -> QuestionBuilder.aQuestion().author(2L).build());
    }

    @Test
    @DisplayName("Las opciones se pueden agregar una a una y la lista resultante es inmutable")
    void opcionesUnaAUna() {
        Question q = QuestionBuilder.aQuestion().statement("¿Pregunta?").author(2L)
                .option("A", "uno").option("B", "dos").build();
        assertEquals(2, q.getOptions().size());
        assertThrows(UnsupportedOperationException.class, () -> q.getOptions().add(new OptionDTO("C", "tres")));
    }

    @Test
    @DisplayName("El DTO lleva todos los campos DCE y el color del estado")
    void dtoConColor() {
        QuestionDTO dto = QuestionBuilder.from(solicitud(2L)).build().toDTO();
        assertEquals("Gamma et al., 1994", dto.getBibliography());
        assertEquals("Creacionales", dto.getSubtopic());
        assertEquals("#969696", dto.getStatusColor());
        assertEquals("Borrador", dto.getStatusLabel());

        dto.setStatus(QuestionStatus.APPROVED);
        assertEquals("#28A745", dto.getStatusColor());
    }

    @Test
    @DisplayName("updateContentFrom reemplaza el contenido pero conserva id y autor")
    void edicion() {
        Question original = QuestionBuilder.from(solicitud(2L)).build();
        original.setId(10L);
        CreateQuestionRequest cambios = solicitud(99L);
        cambios.setTopic("Arquitectura");

        original.updateContentFrom(QuestionBuilder.from(cambios).build());

        assertEquals(10L, original.getId());
        assertEquals(2L, original.getAuthorId());
        assertEquals("Arquitectura", original.getTopic());
    }

    @Test
    @DisplayName("El filtro combina autor, competencia, tema, dificultad y palabra clave sin distinguir mayúsculas")
    void filtro() {
        Question q = QuestionBuilder.from(solicitud(2L)).build();

        assertTrue(QuestionFilter.none().matchesContent(q));
        assertTrue(new QuestionFilter(2L, null, "diseño de software", "PATRONES", "medio", "construcción").matchesContent(q));
        assertFalse(new QuestionFilter(3L, null, null, null, null, null).matchesContent(q));
        assertFalse(new QuestionFilter(null, null, null, null, "ALTO", null).matchesContent(q));
        assertFalse(new QuestionFilter(null, null, null, null, null, "normalización").matchesContent(q));
        assertTrue(new QuestionFilter(null, QuestionStatus.DRAFT, null, null, null, null).matchesStatus(QuestionStatus.DRAFT));
        assertFalse(new QuestionFilter(null, QuestionStatus.DRAFT, null, null, null, null).matchesStatus(QuestionStatus.APPROVED));
    }
}
