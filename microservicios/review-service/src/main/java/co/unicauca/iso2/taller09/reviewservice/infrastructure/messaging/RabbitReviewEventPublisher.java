package co.unicauca.iso2.taller09.reviewservice.infrastructure.messaging;

import co.unicauca.iso2.taller09.common.constants.RabbitMQConstants;
import co.unicauca.iso2.taller09.common.messages.ReviewCompleted;
import co.unicauca.iso2.taller09.common.messages.ReviewerAssigned;
import co.unicauca.iso2.taller09.reviewservice.domain.port.ReviewEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitReviewEventPublisher implements ReviewEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(RabbitReviewEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public RabbitReviewEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void reviewerAssigned(ReviewerAssigned event) {
        publish(RabbitMQConstants.ROUTING_KEY_REVIEWER_ASSIGNED, event, event.getOperationId());
    }

    @Override
    public void reviewCompleted(ReviewCompleted event) {
        publish(RabbitMQConstants.ROUTING_KEY_REVIEW_COMPLETED, event, event.getOperationId());
    }

    private void publish(String routingKey, Object event, String operationId) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConstants.EXCHANGE_NAME, routingKey, event, message -> {
                message.getMessageProperties().setMessageId(operationId);
                return message;
            });
            log.info("Evento publicado en {} con operationId={}", routingKey, operationId);
        } catch (AmqpException e) {
            log.error("No se pudo publicar el evento {} (operationId={}): {}", routingKey, operationId, e.getMessage());
        }
    }
}
