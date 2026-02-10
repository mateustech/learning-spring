package learning.customer.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import learning.customer.domain.model.Customer;
import learning.customer.domain.exception.DuplicateEmailException;
import learning.customer.domain.exception.DuplicateGithubUsernameException;
import learning.customer.infrastructure.github.GitHubClient;
import learning.customer.infrastructure.github.GitHubProfile;
import learning.customer.infrastructure.persistence.CustomerJpaRepository;
import learning.customer.usecases.CreateCustomerUseCase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateCustomerUseCaseTest {

    @Mock
    private CustomerJpaRepository customerRepository;

    @Mock
    private GitHubClient gitHubClient;

    @InjectMocks
    private CreateCustomerUseCase createCustomerUseCase;

    @Test
    void shouldCreateCustomerWithGithubNameAndNormalizedData() {
        when(gitHubClient.fetchProfile("OctoCat")).thenReturn(new GitHubProfile("OctoCat", "The Octocat"));
        when(customerRepository.existsByEmail("user@acme.com")).thenReturn(false);
        when(customerRepository.existsByGithubUsername("octocat")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Customer created = createCustomerUseCase.execute("  User@Acme.com ", "OctoCat");

        assertEquals("user@acme.com", created.getEmail());
        assertEquals("octocat", created.getGithubUsername());
        assertEquals("The Octocat", created.getName());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {
        when(gitHubClient.fetchProfile("octocat")).thenReturn(new GitHubProfile("octocat", "The Octocat"));
        when(customerRepository.existsByEmail("user@acme.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class,
            () -> createCustomerUseCase.execute("user@acme.com", "octocat"));
    }

    @Test
    void shouldThrowWhenGithubUsernameAlreadyExists() {
        when(gitHubClient.fetchProfile("octocat")).thenReturn(new GitHubProfile("octocat", "The Octocat"));
        when(customerRepository.existsByEmail("user@acme.com")).thenReturn(false);
        when(customerRepository.existsByGithubUsername("octocat")).thenReturn(true);

        assertThrows(DuplicateGithubUsernameException.class,
            () -> createCustomerUseCase.execute("user@acme.com", "octocat"));
    }
}
