package main.customer.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import main.customer.domain.model.Customer;
import main.customer.domain.exception.CustomerNotFoundException;
import main.customer.domain.exception.DuplicateGithubUsernameException;
import main.customer.contracts.GitHubGateway;
import main.customer.infrastructure.github.GitHubProfile;
import main.customer.infrastructure.persistence.CustomerJpaRepository;
import main.customer.usecases.UpdateCustomerUseCase;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UpdateCustomerUseCaseTest {

    @Mock
    private CustomerJpaRepository customerRepository;

    @Mock
    private GitHubGateway gitHubGateway;

    @InjectMocks
    private UpdateCustomerUseCase updateCustomerUseCase;

    @Test
    void shouldUpdateCustomerWithGithubData() {
        Customer existing = new Customer();
        ReflectionTestUtils.setField(existing, "id", 1L);
        existing.setEmail("old@acme.com");
        existing.setGithubUsername("olduser");
        existing.setName("Old Name");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(gitHubGateway.fetchProfile("newUser")).thenReturn(new GitHubProfile("newUser", "New Name"));
        when(customerRepository.findByEmail("new@acme.com")).thenReturn(Optional.empty());
        when(customerRepository.findByGithubUsername("newuser")).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Customer updated = updateCustomerUseCase.execute(1L, " New@Acme.com ", "newUser");

        assertEquals("new@acme.com", updated.getEmail());
        assertEquals("newuser", updated.getGithubUsername());
        assertEquals("New Name", updated.getName());
        verify(customerRepository).save(existing);
    }

    @Test
    void shouldThrowWhenCustomerDoesNotExist() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class,
            () -> updateCustomerUseCase.execute(999L, "user@acme.com", "octocat"));
    }

    @Test
    void shouldThrowWhenGithubUsernameBelongsToAnotherCustomer() {
        Customer target = new Customer();
        ReflectionTestUtils.setField(target, "id", 1L);
        target.setEmail("target@acme.com");

        Customer another = new Customer();
        ReflectionTestUtils.setField(another, "id", 2L);
        another.setGithubUsername("octocat");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(target));
        when(gitHubGateway.fetchProfile("octocat")).thenReturn(new GitHubProfile("octocat", "The Octocat"));
        when(customerRepository.findByEmail("target@acme.com")).thenReturn(Optional.of(target));
        when(customerRepository.findByGithubUsername("octocat")).thenReturn(Optional.of(another));

        assertThrows(DuplicateGithubUsernameException.class,
            () -> updateCustomerUseCase.execute(1L, "target@acme.com", "octocat"));
    }
}
