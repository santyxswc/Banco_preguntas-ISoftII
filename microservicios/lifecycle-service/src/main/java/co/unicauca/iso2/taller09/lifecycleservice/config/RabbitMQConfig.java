package co.unicauca.iso2.taller09.lifecycleservice.config;

import co.unicauca.iso2.taller09.common.constants.RabbitMQConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange bancoExchange() {
        return new TopicExchange(RabbitMQConstants.EXCHANGE_NAME, true, false);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(RabbitMQConstants.DEAD_LETTER_EXCHANGE, true, false);
    }

    @Bean
    public Queue questionSubmitQueue() {
        return QueueBuilder.durable(RabbitMQConstants.QUEUE_QUESTION_SUBMIT).build();
    }

    @Bean
    public Binding questionSubmitBinding(Queue questionSubmitQueue, TopicExchange bancoExchange) {
        return BindingBuilder.bind(questionSubmitQueue).to(bancoExchange)
                .with(RabbitMQConstants.ROUTING_KEY_QUESTION_SUBMIT);
    }

    @Bean
    public Queue questionSubmittedEventQueue() {
        return QueueBuilder.durable(RabbitMQConstants.QUEUE_QUESTION_SUBMITTED_EVENT)
                .deadLetterExchange(RabbitMQConstants.DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(RabbitMQConstants.QUEUE_QUESTION_SUBMITTED_EVENT + RabbitMQConstants.DEAD_LETTER_SUFFIX)
                .build();
    }

    @Bean
    public Queue questionSubmittedEventDeadLetterQueue() {
        return QueueBuilder.durable(RabbitMQConstants.QUEUE_QUESTION_SUBMITTED_EVENT + RabbitMQConstants.DEAD_LETTER_SUFFIX).build();
    }

    @Bean
    public Binding questionSubmittedEventBinding(Queue questionSubmittedEventQueue, TopicExchange bancoExchange) {
        return BindingBuilder.bind(questionSubmittedEventQueue).to(bancoExchange)
                .with(RabbitMQConstants.ROUTING_KEY_QUESTION_SUBMITTED_EVENT);
    }

    @Bean
    public Binding questionSubmittedEventDeadLetterBinding(Queue questionSubmittedEventDeadLetterQueue,
                                                           DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(questionSubmittedEventDeadLetterQueue).to(deadLetterExchange)
                .with(RabbitMQConstants.QUEUE_QUESTION_SUBMITTED_EVENT + RabbitMQConstants.DEAD_LETTER_SUFFIX);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
