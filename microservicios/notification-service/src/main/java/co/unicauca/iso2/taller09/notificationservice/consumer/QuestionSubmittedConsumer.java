package co.unicauca.iso2.taller09.notificationservice.consumer;

import co.unicauca.iso2.taller09.common.constants.RabbitMQConstants;
import co.unicauca.iso2.taller09.common.enums.NotificationType;
import co.unicauca.iso2.taller09.common.messages.QuestionSubmittedForReview;
import co.unicauca.iso2.taller09.notificationservice.application.NotificationDispatcher;
import co.unicauca.iso2.taller09.notificationservice.domain.Notification;
import co.unicauca.iso2.taller09.notificationservice.idempotency.ProcessedEventRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class QuestionSubmittedConsumer {
    private static final Logger log = LoggerFactory.getLogger(QuestionSubmittedConsumer.class);
    private static final String EVENT = "QuestionSubmittedForReview";

    private final NotificationDispatcher dispatcher;
    private final ProcessedEventRegistry processedEvents;
    private final Long administratorId;

    public QuestionSubmittedConsumer(NotificationDispatcher dispatcher, ProcessedEventRegistry processedEvents,
                                     @Value("${notifications.admin-id:1}") Long administratorId) {
        this.dispatcher = dispatcher;
        this.processedEvents = processedEvents;
        this.administratorId = administratorId;
    }

    @RabbitListener(queues = RabbitMQConstants.QUEUE_QUESTION_SUBMITTED_EVENT)
    public void handleQuestionSubmitted(QuestionSubmittedForReview event) {
        if (processedEvents.alreadyProcessed(EVENT, event.getOperationId())) {
            log.info("Evento PreguntaEnviadaARevision duplicado ignorado: operationId={}", event.getOperationId());
            return;
        }
        String subject = String.format("La pregunta #%d está pendiente de asignar revisores", event.getQuestionId());
        String body = String.format("La pregunta #%d pasó a %s. Ingrese al Banco de Preguntas, sección Administrador, "
                + "y asígnele entre 1 y 3 revisores. Operación: %s",
                event.getQuestionId(), event.getNewState(), event.getOperationId());

        Notification sent = dispatcher.notify(administratorId, NotificationType.QUESTION_SUBMITTED, subject, body);
        processedEvents.markProcessed(EVENT, event.getOperationId());
        log.info("Notificación #{} ({}) al administrador #{} por la pregunta #{}",
                sent.getId(), sent.getStatus(), administratorId, event.getQuestionId());
    }
}
