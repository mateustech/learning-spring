package com.example.demo.customer.application.usecase;

import com.example.demo.customer.Customer;
import com.example.demo.customer.DuplicateEmailException;
import com.example.demo.customer.DuplicateGithubUsernameException;
import com.example.demo.customer.application.port.CustomerGateway;
import com.example.demo.customer.application.port.GitHubGateway;
import com.example.demo.integration.github.GitHubProfile;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateCustomerUseCase {

    private final CustomerGateway customerGateway;
    private final GitHubGateway gitHubGateway;

    public CreateCustomerUseCase(CustomerGateway customerGateway, GitHubGateway gitHubGateway) {
        this.customerGateway = customerGateway;
        this.gitHubGateway = gitHubGateway;
    }

    @Transactional
    public Customer execute(String email, String githubUsername) {
        String normalizedEmail = normalizeEmail(email);
        GitHubProfile profile = gitHubGateway.fetchProfile(normalizeGithubUsername(githubUsername));
        String normalizedGithubUsername = profile.login().toLowerCase(Locale.ROOT);

        if (customerGateway.existsByEmail(normalizedEmail)) {
            throw new DuplicateEmailException(normalizedEmail);
        }
        if (customerGateway.existsByGithubUsername(normalizedGithubUsername)) {
            throw new DuplicateGithubUsernameException(normalizedGithubUsername);
        }

        Customer customer = new Customer();
        customer.setName(profile.displayName());
        customer.setEmail(normalizedEmail);
        customer.setGithubUsername(normalizedGithubUsername);
        return customerGateway.save(customer);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeGithubUsername(String githubUsername) {
        return githubUsername.trim();
    }
}
