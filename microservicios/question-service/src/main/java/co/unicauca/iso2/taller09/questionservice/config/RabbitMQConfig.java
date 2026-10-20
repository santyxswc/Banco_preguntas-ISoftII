package co.unicauca.iso2.taller09.questionservice.config;

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
    public Queue questionSubmitQueue() {
        return QueueBuilder.durable(RabbitMQConstants.QUEUE_QUESTION_SUBMIT).build();
    }

    @Bean
    public Binding questionSubmitBinding(Queue questionSubmitQueue, TopicExchange bancoExchange) {
        return BindingBuilder.bind(questionSubmitQueue)
                .to(bancoExchange)
                .with(RabbitMQConstants.ROUTING_KEY_QUESTION_SUBMIT);
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
