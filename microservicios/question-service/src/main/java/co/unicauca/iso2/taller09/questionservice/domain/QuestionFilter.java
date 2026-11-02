package co.unicauca.iso2.taller09.questionservice.domain;

import co.unicauca.iso2.taller09.common.enums.QuestionStatus;

import java.util.Locale;

/**
 * Criterios de búsqueda del listado de preguntas (HU-03). Todos son opcionales; los nulos
 * no filtran.
 */
public record QuestionFilter(Long authorId, QuestionStatus status, String competency, String topic,
                             String difficulty, String keyword) {

    public static QuestionFilter none() {
        return new QuestionFilter(null, null, null, null, null, null);
    }

    /** Filtros que dependen solo del contenido de la pregunta (los aplica el repositorio). */
    public boolean matchesContent(Question q) {
        return (authorId == null || authorId.equals(q.getAuthorId()))
                && equalsIgnoreCase(competency, q.getCompetency())
                && equalsIgnoreCase(topic, q.getTopic())
                && equalsIgnoreCase(difficulty, q.getDifficulty())
                && q.mentions(keyword);
    }

    /** El estado lo decide lifecycle-service, por eso se filtra después de consultarlo. */
    public boolean matchesStatus(QuestionStatus actual) {
        return status == null || status == actual;
    }

    private static boolean equalsIgnoreCase(String expected, String actual) {
        if (expected == null || expected.isBlank()) {
            return true;
        }
        return actual != null && actual.toLowerCase(Locale.ROOT).equals(expected.trim().toLowerCase(Locale.ROOT));
    }
}
