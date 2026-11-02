package co.unicauca.iso2.taller09.notificationservice.config;

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
    public Queue reviewerAssignedQueue() {
        return durableWithDeadLetter(RabbitMQConstants.QUEUE_REVIEWER_ASSIGNED);
    }

    @Bean
    public Queue reviewCompletedQueue() {
        return durableWithDeadLetter(RabbitMQConstants.QUEUE_REVIEW_COMPLETED);
    }

    @Bean
    public Queue reviewerAssignedDeadLetterQueue() {
        return QueueBuilder.durable(RabbitMQConstants.QUEUE_REVIEWER_ASSIGNED + RabbitMQConstants.DEAD_LETTER_SUFFIX).build();
    }

    @Bean
    public Queue reviewCompletedDeadLetterQueue() {
        return QueueBuilder.durable(RabbitMQConstants.QUEUE_REVIEW_COMPLETED + RabbitMQConstants.DEAD_LETTER_SUFFIX).build();
    }

    @Bean
    public Binding reviewerAssignedBinding(Queue reviewerAssignedQueue, TopicExchange bancoExchange) {
        return BindingBuilder.bind(reviewerAssignedQueue).to(bancoExchange)
                .with(RabbitMQConstants.ROUTING_KEY_REVIEWER_ASSIGNED);
    }

    @Bean
    public Binding reviewCompletedBinding(Queue reviewCompletedQueue, TopicExchange bancoExchange) {
        return BindingBuilder.bind(reviewCompletedQueue).to(bancoExchange)
                .with(RabbitMQConstants.ROUTING_KEY_REVIEW_COMPLETED);
    }

    @Bean
    public Binding reviewerAssignedDeadLetterBinding(Queue reviewerAssignedDeadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(reviewerAssignedDeadLetterQueue).to(deadLetterExchange)
                .with(RabbitMQConstants.QUEUE_REVIEWER_ASSIGNED + RabbitMQConstants.DEAD_LETTER_SUFFIX);
    }

    @Bean
    public Binding reviewCompletedDeadLetterBinding(Queue reviewCompletedDeadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(reviewCompletedDeadLetterQueue).to(deadLetterExchange)
                .with(RabbitMQConstants.QUEUE_REVIEW_COMPLETED + RabbitMQConstants.DEAD_LETTER_SUFFIX);
    }

    @Bean
    public Queue questionSubmittedEventQueue() {
        return durableWithDeadLetter(RabbitMQConstants.QUEUE_QUESTION_SUBMITTED_EVENT);
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

    private static Queue durableWithDeadLetter(String name) {
        return QueueBuilder.durable(name)
                .deadLetterExchange(RabbitMQConstants.DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(name + RabbitMQConstants.DEAD_LETTER_SUFFIX)
                .build();
    }
}
