package main.customer.infrastructure.messaging;

public record CustomerCreatedEvent(
    Long customerId,
    String githubUsername,
    String correlationId
) {
}
