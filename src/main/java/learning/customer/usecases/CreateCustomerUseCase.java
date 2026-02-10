package learning.customer.usecases;

import learning.customer.domain.model.Customer;
import learning.customer.domain.exception.DuplicateEmailException;
import learning.customer.domain.exception.DuplicateGithubUsernameException;
import learning.customer.infrastructure.github.GitHubClient;
import learning.customer.infrastructure.github.GitHubProfile;
import learning.customer.infrastructure.persistence.CustomerJpaRepository;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateCustomerUseCase {

    private final CustomerJpaRepository customerRepository;
    private final GitHubClient gitHubClient;

    public CreateCustomerUseCase(CustomerJpaRepository customerRepository, GitHubClient gitHubClient) {
        this.customerRepository = customerRepository;
        this.gitHubClient = gitHubClient;
    }

    @Transactional
    public Customer execute(String email, String githubUsername) {
        String normalizedEmail = normalizeEmail(email);
        GitHubProfile profile = gitHubClient.fetchProfile(normalizeGithubUsername(githubUsername));
        String normalizedGithubUsername = profile.login().toLowerCase(Locale.ROOT);

        if (customerRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateEmailException(normalizedEmail);
        }
        if (customerRepository.existsByGithubUsername(normalizedGithubUsername)) {
            throw new DuplicateGithubUsernameException(normalizedGithubUsername);
        }

        Customer customer = new Customer();
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
