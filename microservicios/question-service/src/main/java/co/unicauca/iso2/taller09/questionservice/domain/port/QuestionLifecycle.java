package co.unicauca.iso2.taller09.questionservice.domain.port;

import co.unicauca.iso2.taller09.common.enums.QuestionStatus;

import java.util.Collection;
import java.util.Map;

/** Puerto hacia el contexto de Ciclo de vida, la autoridad del estado de cada pregunta. */
public interface QuestionLifecycle {

    QuestionStatus stateOf(Long questionId);

    /** Estados de varias preguntas en una sola consulta (las desconocidas vienen en DRAFT). */
    Map<Long, QuestionStatus> statesOf(Collection<Long> questionIds);
}
