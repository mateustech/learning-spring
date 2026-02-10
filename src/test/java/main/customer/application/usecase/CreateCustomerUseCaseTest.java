package main.customer.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import main.customer.domain.model.Customer;
import main.customer.domain.exception.DuplicateEmailException;
import main.customer.domain.exception.DuplicateGithubUsernameException;
import main.customer.infrastructure.messaging.CustomerCreatedEventPublisher;
import main.customer.infrastructure.persistence.CustomerJpaRepository;
import main.customer.usecases.CreateCustomerUseCase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

@ExtendWith(MockitoExtension.class)
class CreateCustomerUseCaseTest {

    @Mock
    private CustomerJpaRepository customerRepository;

    @Mock
    private CustomerCreatedEventPublisher eventPublisher;

    @InjectMocks
    private CreateCustomerUseCase createCustomerUseCase;

    @Test
    void shouldCreateCustomerAndPublishEvent() {
        MDC.put("correlationId", "corr-test-123");
        try {
            when(customerRepository.existsByEmail("user@acme.com")).thenReturn(false);
            when(customerRepository.existsByGithubUsername("octocat")).thenReturn(false);
            when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
                Customer customer = invocation.getArgument(0);
                org.springframework.test.util.ReflectionTestUtils.setField(customer, "id", 10L);
                return customer;
            });

            Customer created = createCustomerUseCase.execute("  User@Acme.com ", "OctoCat");

            assertEquals("user@acme.com", created.getEmail());
            assertEquals("octocat", created.getGithubUsername());
            assertEquals("octocat", created.getName());
            verify(customerRepository).save(any(Customer.class));
            verify(eventPublisher).publish(argThat(event ->
                event.customerId().equals(10L)
                    && event.githubUsername().equals("octocat")
                    && event.correlationId().equals("corr-test-123")
            ));
        } finally {
            MDC.clear();
        }
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {
        when(customerRepository.existsByEmail("user@acme.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class,
            () -> createCustomerUseCase.execute("user@acme.com", "octocat"));
    }

    @Test
    void shouldThrowWhenGithubUsernameAlreadyExists() {
        when(customerRepository.existsByEmail("user@acme.com")).thenReturn(false);
        when(customerRepository.existsByGithubUsername("octocat")).thenReturn(true);

        assertThrows(DuplicateGithubUsernameException.class,
            () -> createCustomerUseCase.execute("user@acme.com", "octocat"));
    }
}
