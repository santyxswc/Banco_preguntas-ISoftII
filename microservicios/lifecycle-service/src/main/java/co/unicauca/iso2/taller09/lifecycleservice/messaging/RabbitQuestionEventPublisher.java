package co.unicauca.iso2.taller09.lifecycleservice.messaging;

import co.unicauca.iso2.taller09.common.constants.RabbitMQConstants;
import co.unicauca.iso2.taller09.common.messages.QuestionSubmittedForReview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitQuestionEventPublisher implements QuestionEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(RabbitQuestionEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public RabbitQuestionEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void questionSubmittedForReview(QuestionSubmittedForReview event) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConstants.EXCHANGE_NAME,
                    RabbitMQConstants.ROUTING_KEY_QUESTION_SUBMITTED_EVENT, event, message -> {
                        message.getMessageProperties().setMessageId(event.getOperationId());
                        return message;
                    });
            log.info("Evento PreguntaEnviadaARevision publicado: questionId={}, operationId={}",
                    event.getQuestionId(), event.getOperationId());
        } catch (AmqpException e) {
            log.error("No se pudo publicar PreguntaEnviadaARevision para la pregunta {}: {}",
                    event.getQuestionId(), e.getMessage());
        }
    }
}
