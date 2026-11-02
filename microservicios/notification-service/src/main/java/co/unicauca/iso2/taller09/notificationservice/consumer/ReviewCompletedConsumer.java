package co.unicauca.iso2.taller09.notificationservice.consumer;

import co.unicauca.iso2.taller09.common.constants.RabbitMQConstants;
import co.unicauca.iso2.taller09.common.enums.NotificationStatus;
import co.unicauca.iso2.taller09.common.enums.NotificationType;
import co.unicauca.iso2.taller09.common.enums.QuestionStatus;
import co.unicauca.iso2.taller09.common.messages.ReviewCompleted;
import co.unicauca.iso2.taller09.notificationservice.domain.Notification;
import co.unicauca.iso2.taller09.notificationservice.idempotency.ProcessedEventRegistry;
import co.unicauca.iso2.taller09.notificationservice.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Component
public class ReviewCompletedConsumer {
    private static final Logger log = LoggerFactory.getLogger(ReviewCompletedConsumer.class);

    private final NotificationRepository notificationRepository;
    private final ProcessedEventRegistry processedEvents;
    private final Long administratorId;

    public ReviewCompletedConsumer(NotificationRepository notificationRepository,
                                   ProcessedEventRegistry processedEvents,
                                   @Value("${notifications.admin-id:1}") Long administratorId) {
        this.notificationRepository = notificationRepository;
        this.processedEvents = processedEvents;
        this.administratorId = administratorId;
    }

    @RabbitListener(queues = RabbitMQConstants.QUEUE_REVIEW_COMPLETED)
    public void handleReviewCompleted(ReviewCompleted event) {
        if (processedEvents.alreadyProcessed("ReviewCompleted", event.getOperationId())) {
            log.info("Evento RevisionCompletada duplicado ignorado: operationId={}", event.getOperationId());
            return;
        }

        NotificationType type = typeOf(event.getQuestionFinalState());
        String subject = event.getQuestionFinalState() == null
                ? String.format("La pregunta #%d recibió una evaluación (%s)", event.getQuestionId(), event.getResult())
                : String.format("La pregunta #%d quedó %s tras la revisión por pares", event.getQuestionId(), event.getQuestionFinalState());
        String body = String.format("El revisor #%d registró la decisión %s sobre la pregunta #%d. Observaciones: %s",
                event.getReviewerId(), event.getResult(), event.getQuestionId(),
                event.getComments() == null ? "sin observaciones" : event.getComments());

        Set<Long> recipients = new LinkedHashSet<>();
        if (event.getAuthorId() != null) {
            recipients.add(event.getAuthorId());
        }
        recipients.add(administratorId);
        recipients.stream().filter(Objects::nonNull).forEach(recipient ->
                notificationRepository.save(new Notification(null, recipient, type, subject, body, NotificationStatus.SENT)));

        processedEvents.markProcessed("ReviewCompleted", event.getOperationId());
        log.info("Evento RevisionCompletada procesado: questionId={}, destinatarios={}", event.getQuestionId(), recipients);
    }

    private static NotificationType typeOf(QuestionStatus finalState) {
        if (finalState == QuestionStatus.APPROVED) {
            return NotificationType.QUESTION_APPROVED;
        }
        if (finalState == QuestionStatus.REJECTED) {
            return NotificationType.QUESTION_REJECTED;
        }
        return NotificationType.REVIEW_COMPLETED;
    }
}
