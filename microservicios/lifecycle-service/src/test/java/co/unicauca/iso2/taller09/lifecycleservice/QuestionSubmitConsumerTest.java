package co.unicauca.iso2.taller09.lifecycleservice;

import co.unicauca.iso2.taller09.common.enums.OperationStatus;
import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import co.unicauca.iso2.taller09.common.messages.SubmitQuestionForReview;
import co.unicauca.iso2.taller09.lifecycleservice.consumer.QuestionSubmitConsumer;
import co.unicauca.iso2.taller09.lifecycleservice.domain.OperationResult;
import co.unicauca.iso2.taller09.lifecycleservice.domain.QuestionState;
import co.unicauca.iso2.taller09.lifecycleservice.repository.LifecycleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class QuestionSubmitConsumerTest {

    @Autowired
    private QuestionSubmitConsumer consumer;

    @Autowired
    private LifecycleRepository lifecycleRepository;

    @Test
    void testConsumoExitosoTransicionDraftAPendingReview() {
        Long questionId = 777L;
        String operationId = "op-test-success-1";
        lifecycleRepository.saveState(new QuestionState(null, questionId, QuestionStatus.DRAFT, "SYSTEM", "Borrador inicial"));

        SubmitQuestionForReview cmd = new SubmitQuestionForReview(operationId, questionId, 2L, LocalDateTime.now());
        consumer.handleSubmitQuestionForReview(cmd);

        QuestionState state = lifecycleRepository.findStateByQuestionId(questionId).orElseThrow();
        assertEquals(QuestionStatus.PENDING_REVIEW, state.getState());

        OperationResult op = lifecycleRepository.findOperationById(operationId).orElseThrow();
        assertEquals(OperationStatus.COMPLETED, op.getStatus());
    }

    @Test
    void testConsumoFallidoTransicionInvalida() {
        // Matriz de pruebas: "Falla el procesamiento de una transición asíncrona: El cliente puede consultar el resultado y no se informa falsamente que la transición se completó"
        Long questionId = 778L;
        String operationId = "op-test-failed-1";
        // Si ya está en IN_REVIEW, no puede pasar a PENDING_REVIEW
        lifecycleRepository.saveState(new QuestionState(null, questionId, QuestionStatus.IN_REVIEW, "SYSTEM", "Ya en revisión"));

        SubmitQuestionForReview cmd = new SubmitQuestionForReview(operationId, questionId, 2L, LocalDateTime.now());
        consumer.handleSubmitQuestionForReview(cmd);

        OperationResult op = lifecycleRepository.findOperationById(operationId).orElseThrow();
        assertEquals(OperationStatus.FAILED, op.getStatus());
        assertTrue(op.getMessage().contains("Transición rechazada"));
    }
}
