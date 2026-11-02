package co.unicauca.iso2.taller09.validationservice;

import co.unicauca.iso2.taller09.common.dto.CreateQuestionRequest;
import co.unicauca.iso2.taller09.common.dto.OptionDTO;
import co.unicauca.iso2.taller09.common.dto.ValidationResult;
import co.unicauca.iso2.taller09.validationservice.domain.QuestionValidator;
import co.unicauca.iso2.taller09.validationservice.domain.ValidationRule;
import co.unicauca.iso2.taller09.validationservice.domain.rules.CorrectAnswerRule;
import co.unicauca.iso2.taller09.validationservice.domain.rules.EvidenceRule;
import co.unicauca.iso2.taller09.validationservice.domain.rules.OptionsRule;
import co.unicauca.iso2.taller09.validationservice.domain.rules.StatementRule;
import co.unicauca.iso2.taller09.validationservice.domain.rules.TaxonomyRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas unitarias del patrón Strategy de validación estructural (sin Spring). */
class QuestionValidatorTest {

    private final QuestionValidator validator = new QuestionValidator(List.of(
            new StatementRule(), new OptionsRule(), new CorrectAnswerRule(), new EvidenceRule(), new TaxonomyRule()));

    static CreateQuestionRequest valida() {
        return new CreateQuestionRequest(
                "En un sistema de pagos se requiere notificar a varios módulos cuando cambia un saldo.",
                "¿Qué patrón permite notificar a varios objetos cuando cambia el estado de otro?",
                List.of(new OptionDTO("A", "Observer"), new OptionDTO("B", "Builder"),
                        new OptionDTO("C", "Adapter"), new OptionDTO("D", "Facade")),
                "A",
                "Observer define una dependencia uno a muchos entre objetos.",
                "Gamma et al. Design Patterns, 1994",
                "Diseño de software", "Patrones", "Comportamiento", "medio", 2L);
    }

    @Test
    @DisplayName("Una pregunta con todos los elementos DCE es válida")
    void preguntaCompleta() {
        ValidationResult result = validator.validate(valida());
        assertTrue(result.isValid(), () -> "Errores: " + result.getErrors());
    }

    @Test
    @DisplayName("Una solicitud nula se rechaza sin lanzar excepción")
    void solicitudNula() {
        assertFalse(validator.validate(null).isValid());
    }

    @Test
    @DisplayName("Enunciado corto")
    void enunciadoCorto() {
        CreateQuestionRequest q = valida();
        q.setText("¿Corta?");
        assertEquals(1, new StatementRule().check(q).size());
    }

    @Test
    @DisplayName("Menos de cuatro opciones")
    void menosDeCuatroOpciones() {
        CreateQuestionRequest q = valida();
        q.setOptions(q.getOptions().subList(0, 3));
        assertFalse(new OptionsRule().check(q).isEmpty());
    }

    @Test
    @DisplayName("Opciones repetidas y comodines prohibidos")
    void opcionesRepetidasYComodin() {
        CreateQuestionRequest q = valida();
        q.setOptions(List.of(new OptionDTO("A", "Observer"), new OptionDTO("B", "observer"),
                new OptionDTO("C", "Todas las anteriores"), new OptionDTO("D", "Facade")));
        assertEquals(2, new OptionsRule().check(q).size());
    }

    @Test
    @DisplayName("La respuesta correcta debe existir entre las opciones")
    void claveInexistente() {
        CreateQuestionRequest q = valida();
        q.setCorrectOptionId("E");
        assertEquals(1, new CorrectAnswerRule().check(q).size());
    }

    @Test
    @DisplayName("Contexto, justificación y bibliografía son obligatorios")
    void elementosDce() {
        CreateQuestionRequest q = valida();
        q.setContext("corto");
        q.setJustification(null);
        q.setBibliography(" ");
        assertEquals(3, new EvidenceRule().check(q).size());
    }

    @Test
    @DisplayName("La taxonomía exige competencia, tema, subtema y un nivel válido")
    void taxonomia() {
        CreateQuestionRequest q = valida();
        q.setCompetency(null);
        q.setDifficulty("EXTREMO");
        assertEquals(2, new TaxonomyRule().check(q).size());
    }

    @Test
    @DisplayName("Se pueden agregar estrategias nuevas sin modificar el validador")
    void estrategiaNueva() {
        ValidationRule sinPalabraNo = new ValidationRule() {
            public String name() { return "Sin negaciones"; }
            public List<String> check(CreateQuestionRequest q) {
                return q.getText().contains(" NO ") ? List.of("Evite negaciones en mayúscula") : List.of();
            }
        };
        QuestionValidator extendido = new QuestionValidator(List.of(new StatementRule(), sinPalabraNo));
        CreateQuestionRequest q = valida();
        q.setText("¿Cuál opción NO corresponde a un patrón creacional?");
        assertEquals(List.of("Evite negaciones en mayúscula"), extendido.validate(q).getErrors());
        assertEquals(List.of("Enunciado", "Sin negaciones"), extendido.ruleNames());
    }
}
