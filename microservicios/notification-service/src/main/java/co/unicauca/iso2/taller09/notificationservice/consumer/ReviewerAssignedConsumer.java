package co.unicauca.iso2.taller09.notificationservice.consumer;

import co.unicauca.iso2.taller09.common.constants.RabbitMQConstants;
import co.unicauca.iso2.taller09.common.enums.NotificationStatus;
import co.unicauca.iso2.taller09.common.enums.NotificationType;
import co.unicauca.iso2.taller09.common.messages.ReviewerAssigned;
import co.unicauca.iso2.taller09.notificationservice.domain.Notification;
import co.unicauca.iso2.taller09.notificationservice.idempotency.ProcessedEventRegistry;
import co.unicauca.iso2.taller09.notificationservice.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ReviewerAssignedConsumer {
    private static final Logger log = LoggerFactory.getLogger(ReviewerAssignedConsumer.class);

    private final NotificationRepository notificationRepository;
    private final ProcessedEventRegistry processedEvents;

    public ReviewerAssignedConsumer(NotificationRepository notificationRepository,
                                    ProcessedEventRegistry processedEvents) {
        this.notificationRepository = notificationRepository;
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
        String body = String.format("Estimado docente revisor (ID: %d), se le ha asignado la pregunta #%d para su dictamen por parte del Administrador %d. Operación: %s",
                event.getReviewerId(), event.getQuestionId(), event.getAssignedBy(), event.getOperationId());

        Notification saved = notificationRepository.save(new Notification(null, event.getReviewerId(),
                NotificationType.REVIEWER_ASSIGNED, subject, body, NotificationStatus.SENT));

        processedEvents.markProcessed("ReviewerAssigned", event.getOperationId());
        log.info("Notificación #{} registrada para el revisor #{}", saved.getId(), event.getReviewerId());
    }
}
