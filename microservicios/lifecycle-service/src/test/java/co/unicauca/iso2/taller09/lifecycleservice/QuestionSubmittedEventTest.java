package co.unicauca.iso2.taller09.lifecycleservice;

import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import co.unicauca.iso2.taller09.common.messages.QuestionSubmittedForReview;
import co.unicauca.iso2.taller09.common.messages.SubmitQuestionForReview;
import co.unicauca.iso2.taller09.lifecycleservice.consumer.QuestionSubmitConsumer;
import co.unicauca.iso2.taller09.lifecycleservice.domain.QuestionState;
import co.unicauca.iso2.taller09.lifecycleservice.repository.LifecycleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestionSubmittedEventTest {

    private LifecycleRepository repository;
    private List<QuestionSubmittedForReview> published;
    private QuestionSubmitConsumer consumer;

    @BeforeEach
    void setUp() {
        repository = new LifecycleRepository();
        published = new ArrayList<>();
        consumer = new QuestionSubmitConsumer(repository, published::add);
    }

    @Test
    @DisplayName("Al pasar a PENDING_REVIEW se publica PreguntaEnviadaARevision con el mismo operationId")
    void publicaEventoAlEnviarARevision() {
        repository.saveState(new QuestionState(null, 501L, QuestionStatus.DRAFT, "SYSTEM", "Borrador"));

        consumer.handleSubmitQuestionForReview(new SubmitQuestionForReview("op-501", 501L, 2L, LocalDateTime.now()));

        assertEquals(1, published.size());
        QuestionSubmittedForReview event = published.get(0);
        assertEquals(501L, event.getQuestionId().longValue());
        assertEquals(QuestionStatus.PENDING_REVIEW, event.getNewState());
        assertEquals("op-501", event.getOperationId());
    }

    @Test
    @DisplayName("Si la transición se rechaza no se publica ningún evento")
    void noPublicaSiLaTransicionFalla() {
        repository.saveState(new QuestionState(null, 502L, QuestionStatus.IN_REVIEW, "SYSTEM", "En revisión"));

        consumer.handleSubmitQuestionForReview(new SubmitQuestionForReview("op-502", 502L, 2L, LocalDateTime.now()));

        assertTrue(published.isEmpty());
        assertEquals(QuestionStatus.IN_REVIEW, repository.findStateByQuestionId(502L).orElseThrow().getState());
    }

    @Test
    @DisplayName("Una pregunta rechazada que se reenvía también genera el evento")
    void publicaAlReenviarUnaRechazada() {
        repository.saveState(new QuestionState(null, 503L, QuestionStatus.REJECTED, "SYSTEM", "Rechazada"));

        consumer.handleSubmitQuestionForReview(new SubmitQuestionForReview("op-503", 503L, 2L, LocalDateTime.now()));

        assertEquals(1, published.size());
        assertEquals(QuestionStatus.PENDING_REVIEW, repository.findStateByQuestionId(503L).orElseThrow().getState());
    }
}
