package learning.customer.application.usecase;

import learning.customer.domain.model.Customer;
import learning.customer.domain.exception.CustomerNotFoundException;
import learning.customer.domain.exception.DuplicateEmailException;
import learning.customer.domain.exception.DuplicateGithubUsernameException;
import learning.customer.infrastructure.github.GitHubClient;
import learning.customer.infrastructure.github.GitHubProfile;
import learning.customer.infrastructure.persistence.CustomerJpaRepository;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateCustomerUseCase {

    private final CustomerJpaRepository customerRepository;
    private final GitHubClient gitHubClient;

    public UpdateCustomerUseCase(CustomerJpaRepository customerRepository, GitHubClient gitHubClient) {
        this.customerRepository = customerRepository;
        this.gitHubClient = gitHubClient;
    }

    @Transactional
    public Customer execute(Long id, String email, String githubUsername) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
        String normalizedEmail = normalizeEmail(email);
        GitHubProfile profile = gitHubClient.fetchProfile(normalizeGithubUsername(githubUsername));
        String normalizedGithubUsername = profile.login().toLowerCase(Locale.ROOT);

        var customerWithEmail = customerRepository.findByEmail(normalizedEmail);
        if (customerWithEmail.isPresent() && !customerWithEmail.get().getId().equals(id)) {
            throw new DuplicateEmailException(normalizedEmail);
        }

        var customerWithGithub = customerRepository.findByGithubUsername(normalizedGithubUsername);
        if (customerWithGithub.isPresent() && !customerWithGithub.get().getId().equals(id)) {
            throw new DuplicateGithubUsernameException(normalizedGithubUsername);
        }

        customer.setName(profile.displayName());
        customer.setEmail(normalizedEmail);
        customer.setGithubUsername(normalizedGithubUsername);
        return customerRepository.save(customer);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeGithubUsername(String githubUsername) {
        return githubUsername.trim();
    }
}
