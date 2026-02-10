package com.example.demo.customer.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.customer.Customer;
import com.example.demo.customer.DuplicateEmailException;
import com.example.demo.customer.DuplicateGithubUsernameException;
import com.example.demo.customer.application.port.CustomerGateway;
import com.example.demo.customer.application.port.GitHubGateway;
import com.example.demo.integration.github.GitHubProfile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateCustomerUseCaseTest {

    @Mock
    private CustomerGateway customerGateway;

    @Mock
    private GitHubGateway gitHubGateway;

    @InjectMocks
    private CreateCustomerUseCase createCustomerUseCase;

    @Test
    void shouldCreateCustomerWithGithubNameAndNormalizedData() {
        when(gitHubGateway.fetchProfile("OctoCat")).thenReturn(new GitHubProfile("OctoCat", "The Octocat"));
        when(customerGateway.existsByEmail("user@acme.com")).thenReturn(false);
        when(customerGateway.existsByGithubUsername("octocat")).thenReturn(false);
        when(customerGateway.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Customer created = createCustomerUseCase.execute("  User@Acme.com ", "OctoCat");

        assertEquals("user@acme.com", created.getEmail());
        assertEquals("octocat", created.getGithubUsername());
        assertEquals("The Octocat", created.getName());
        verify(customerGateway).save(any(Customer.class));
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {
        when(gitHubGateway.fetchProfile("octocat")).thenReturn(new GitHubProfile("octocat", "The Octocat"));
        when(customerGateway.existsByEmail("user@acme.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class,
            () -> createCustomerUseCase.execute("user@acme.com", "octocat"));
    }

    @Test
    void shouldThrowWhenGithubUsernameAlreadyExists() {
        when(gitHubGateway.fetchProfile("octocat")).thenReturn(new GitHubProfile("octocat", "The Octocat"));
        when(customerGateway.existsByEmail("user@acme.com")).thenReturn(false);
        when(customerGateway.existsByGithubUsername("octocat")).thenReturn(true);

        assertThrows(DuplicateGithubUsernameException.class,
            () -> createCustomerUseCase.execute("user@acme.com", "octocat"));
    }
}
