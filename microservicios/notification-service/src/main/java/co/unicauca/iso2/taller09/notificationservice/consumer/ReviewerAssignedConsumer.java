package co.unicauca.iso2.taller09.notificationservice.consumer;

import co.unicauca.iso2.taller09.common.constants.RabbitMQConstants;
import co.unicauca.iso2.taller09.common.enums.NotificationStatus;
import co.unicauca.iso2.taller09.common.enums.NotificationType;
import co.unicauca.iso2.taller09.common.messages.ReviewerAssigned;
import co.unicauca.iso2.taller09.notificationservice.domain.Notification;
import co.unicauca.iso2.taller09.notificationservice.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ReviewerAssignedConsumer {
    private static final Logger log = LoggerFactory.getLogger(ReviewerAssignedConsumer.class);

    private final NotificationRepository notificationRepository;

    public ReviewerAssignedConsumer(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Paso 8 del Taller: Consumo asíncrono del evento ReviewerAssigned publicado en RabbitMQ.
     * Procesa la notificación sin bloquear la operación principal de asignación.
     */
    @RabbitListener(queues = RabbitMQConstants.QUEUE_REVIEWER_ASSIGNED)
    public void handleReviewerAssigned(ReviewerAssigned event) {
        log.info(">>> [NOTIFICATION-SERVICE] Evento ReviewerAssigned recibido desde RabbitMQ: questionId={}, reviewerId={}, assignedBy={}",
                event.getQuestionId(), event.getReviewerId(), event.getAssignedBy());

        String subject = String.format("Nueva pregunta Saber Pro asignada para evaluación (Pregunta #%d)", event.getQuestionId());
        String body = String.format("Estimado docente revisor (ID: %d), se le ha asignado la pregunta #%d para su dictamen por parte del Administrador %d. Operación: %s",
                event.getReviewerId(), event.getQuestionId(), event.getAssignedBy(), event.getOperationId());

        Notification notification = new Notification(
                null,
                event.getReviewerId(),
                NotificationType.REVIEWER_ASSIGNED,
                subject,
                body,
                NotificationStatus.SENT
        );

        Notification saved = notificationRepository.save(notification);

        log.info("<<< [NOTIFICATION-SERVICE] Notificación #{} enviada con éxito al revisor #{} (DESACOPLADA Y ASÍNCRONA).",
                saved.getId(), event.getReviewerId());
    }
}
