package co.unicauca.iso2.taller09.lifecycleservice.consumer;

import co.unicauca.iso2.taller09.common.constants.RabbitMQConstants;
import co.unicauca.iso2.taller09.common.enums.OperationStatus;
import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import co.unicauca.iso2.taller09.common.messages.QuestionSubmittedForReview;
import co.unicauca.iso2.taller09.common.messages.SubmitQuestionForReview;
import co.unicauca.iso2.taller09.lifecycleservice.domain.OperationResult;
import co.unicauca.iso2.taller09.lifecycleservice.domain.QuestionState;
import co.unicauca.iso2.taller09.lifecycleservice.messaging.QuestionEventPublisher;
import co.unicauca.iso2.taller09.lifecycleservice.repository.LifecycleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class QuestionSubmitConsumer {
    private static final Logger log = LoggerFactory.getLogger(QuestionSubmitConsumer.class);

    private final LifecycleRepository lifecycleRepository;
    private final QuestionEventPublisher eventPublisher;

    public QuestionSubmitConsumer(LifecycleRepository lifecycleRepository, QuestionEventPublisher eventPublisher) {
        this.lifecycleRepository = lifecycleRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Paso 6 del Taller: Consumo asíncrono del comando SubmitQuestionForReview.
     * Verifica la transición DRAFT -> PENDING_REVIEW y registra el resultado de la operación.
     */
    @RabbitListener(queues = RabbitMQConstants.QUEUE_QUESTION_SUBMIT)
    public void handleSubmitQuestionForReview(SubmitQuestionForReview command) {
        log.info(">>> [LIFECYCLE-SERVICE] Consumido comando SubmitQuestionForReview desde RabbitMQ: operationId={}, questionId={}",
                command.getOperationId(), command.getQuestionId());

        OperationResult operation = new OperationResult(
                command.getOperationId(),
                command.getQuestionId(),
                "SUBMIT_FOR_REVIEW",
                OperationStatus.PENDING,
                "Procesando transición de estado..."
        );
        lifecycleRepository.saveOperation(operation);

        QuestionState currentState = lifecycleRepository.getOrCreateState(command.getQuestionId());

        // Caso de falla controlada: Si la pregunta NO puede transicionar a PENDING_REVIEW
        if (!currentState.canTransitionTo(QuestionStatus.PENDING_REVIEW)) {
            String errorMsg = String.format("Transición rechazada: la pregunta con ID %d está en estado %s y no puede pasar a PENDING_REVIEW",
                    command.getQuestionId(), currentState.getState());
            log.warn("Falla en transición asíncrona: {}", errorMsg);

            operation.fail(errorMsg);
            lifecycleRepository.saveOperation(operation);
            return;
        }

        // Transición válida: DRAFT -> PENDING_REVIEW
        currentState.setState(QuestionStatus.PENDING_REVIEW);
        currentState.setChangedBy("Autor " + command.getRequestedBy());
        currentState.setMessage("Pregunta enviada a revisión mediante mensajería asíncrona RabbitMQ");
        lifecycleRepository.saveState(currentState);

        operation.complete("Transición completada exitosamente a PENDING_REVIEW");
        lifecycleRepository.saveOperation(operation);

        eventPublisher.questionSubmittedForReview(new QuestionSubmittedForReview(
                command.getOperationId(), command.getQuestionId(), QuestionStatus.PENDING_REVIEW, LocalDateTime.now()));

        log.info("<<< [LIFECYCLE-SERVICE] Transición exitosa a PENDING_REVIEW para questionId={}. Operación {} marcada como COMPLETED",
                command.getQuestionId(), command.getOperationId());
    }
}
