package main.customer.contracts;

import main.customer.infrastructure.messaging.CustomerCreatedEvent;

public interface CustomerEventPublisher {

    void publish(CustomerCreatedEvent event);
}
