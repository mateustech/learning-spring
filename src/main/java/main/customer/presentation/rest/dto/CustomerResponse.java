package main.customer.presentation.rest.dto;

import main.customer.domain.model.Customer;
import java.time.OffsetDateTime;

public record CustomerResponse(
    Long id,
    String name,
    String email,
    String githubUsername,
    boolean active,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {

    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
            customer.getId(),
            customer.getName(),
            customer.getEmail(),
            customer.getGithubUsername(),
            customer.isActive(),
            customer.getCreatedAt(),
            customer.getUpdatedAt()
        );
    }
}
