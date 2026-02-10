package main.customer.infrastructure.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class CustomerCreatedEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(CustomerCreatedEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public CustomerCreatedEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(CustomerCreatedEvent event) {
        log.info(
            "event=customer_created_event_publish_started correlationId={} customerId={} githubUsername={} exchange={} routingKey={}",
            event.correlationId(),
            event.customerId(),
            event.githubUsername(),
            CustomerMessagingConfig.EXCHANGE,
            CustomerMessagingConfig.CREATED_ROUTING_KEY
        );
        rabbitTemplate.convertAndSend(
            CustomerMessagingConfig.EXCHANGE,
            CustomerMessagingConfig.CREATED_ROUTING_KEY,
            event
        );
        log.info(
            "event=customer_created_event_published correlationId={} customerId={}",
            event.correlationId(),
            event.customerId()
        );
    }
}
