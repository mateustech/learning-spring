package main.customer.infrastructure.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class CustomerMessagingConfig {

    public static final String EXCHANGE = "customer.exchange";
    public static final String CREATED_QUEUE = "customer.created.queue";
    public static final String CREATED_ROUTING_KEY = "customer.created";

    @Bean
    Queue customerCreatedQueue() {
        return QueueBuilder.durable(CREATED_QUEUE).build();
    }

    @Bean
    DirectExchange customerExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    Binding customerCreatedBinding(Queue customerCreatedQueue, DirectExchange customerExchange) {
        return BindingBuilder.bind(customerCreatedQueue).to(customerExchange).with(CREATED_ROUTING_KEY);
    }

    @Bean
    MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
