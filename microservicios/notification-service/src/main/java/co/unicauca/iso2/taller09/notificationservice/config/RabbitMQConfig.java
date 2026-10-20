package co.unicauca.iso2.taller09.notificationservice.config;

import co.unicauca.iso2.taller09.common.constants.RabbitMQConstants;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange bancoExchange() {
        return new TopicExchange(RabbitMQConstants.EXCHANGE_NAME);
    }

    @Bean
    public Queue reviewerAssignedQueue() {
        return QueueBuilder.durable(RabbitMQConstants.QUEUE_REVIEWER_ASSIGNED).build();
    }

    @Bean
    public Binding reviewerAssignedBinding(Queue reviewerAssignedQueue, TopicExchange bancoExchange) {
        return BindingBuilder.bind(reviewerAssignedQueue)
                .to(bancoExchange)
                .with(RabbitMQConstants.ROUTING_KEY_REVIEWER_ASSIGNED);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
