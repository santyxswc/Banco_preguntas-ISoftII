package co.unicauca.iso2.taller09.notificationservice.consumer;

import co.unicauca.iso2.taller09.common.constants.RabbitMQConstants;
import co.unicauca.iso2.taller09.common.enums.NotificationType;
import co.unicauca.iso2.taller09.common.messages.ReviewerAssigned;
import co.unicauca.iso2.taller09.notificationservice.application.NotificationDispatcher;
import co.unicauca.iso2.taller09.notificationservice.domain.Notification;
import co.unicauca.iso2.taller09.notificationservice.idempotency.ProcessedEventRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Patrón GoF <b>Observer</b> (comportamiento) sobre RabbitMQ: review-service publica
 * {@code RevisorAsignado} sin saber quién lo escucha; este consumidor es un observador suscrito
 * a la cola y reacciona enviando el correo al revisor (HU-04).
 */
@Component
public class ReviewerAssignedConsumer {
    private static final Logger log = LoggerFactory.getLogger(ReviewerAssignedConsumer.class);

    private final NotificationDispatcher dispatcher;
    private final ProcessedEventRegistry processedEvents;

    public ReviewerAssignedConsumer(NotificationDispatcher dispatcher, ProcessedEventRegistry processedEvents) {
        this.dispatcher = dispatcher;
        this.processedEvents = processedEvents;
    }

    @RabbitListener(queues = RabbitMQConstants.QUEUE_REVIEWER_ASSIGNED)
    public void handleReviewerAssigned(ReviewerAssigned event) {
        if (processedEvents.alreadyProcessed("ReviewerAssigned", event.getOperationId())) {
            log.info("Evento RevisorAsignado duplicado ignorado: operationId={}", event.getOperationId());
            return;
        }
        log.info("Evento RevisorAsignado recibido: questionId={}, reviewerId={}, assignedBy={}",
                event.getQuestionId(), event.getReviewerId(), event.getAssignedBy());

        String subject = String.format("Nueva pregunta Saber Pro asignada para evaluación (Pregunta #%d)", event.getQuestionId());
        String body = String.format("Estimado docente revisor (ID: %d), el administrador %d le asignó la pregunta #%d para su dictamen. "
                        + "Ingrese al Banco de Preguntas, sección Revisor, para evaluarla. Operación: %s",
                event.getReviewerId(), event.getAssignedBy(), event.getQuestionId(), event.getOperationId());

        Notification sent = dispatcher.notify(event.getReviewerId(), NotificationType.REVIEWER_ASSIGNED, subject, body);
        processedEvents.markProcessed("ReviewerAssigned", event.getOperationId());
        log.info("Notificación #{} ({}) para el revisor #{}", sent.getId(), sent.getStatus(), event.getReviewerId());
    }
}
