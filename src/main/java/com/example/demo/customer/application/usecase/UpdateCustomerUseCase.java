package com.example.demo.customer.application.usecase;

import com.example.demo.customer.Customer;
import com.example.demo.customer.CustomerNotFoundException;
import com.example.demo.customer.DuplicateEmailException;
import com.example.demo.customer.DuplicateGithubUsernameException;
import com.example.demo.customer.application.port.CustomerGateway;
import com.example.demo.customer.application.port.GitHubGateway;
import com.example.demo.integration.github.GitHubProfile;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateCustomerUseCase {

    private final CustomerGateway customerGateway;
    private final GitHubGateway gitHubGateway;

    public UpdateCustomerUseCase(CustomerGateway customerGateway, GitHubGateway gitHubGateway) {
        this.customerGateway = customerGateway;
        this.gitHubGateway = gitHubGateway;
    }

    @Transactional
    public Customer execute(Long id, String email, String githubUsername) {
        Customer customer = customerGateway.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
        String normalizedEmail = normalizeEmail(email);
        GitHubProfile profile = gitHubGateway.fetchProfile(normalizeGithubUsername(githubUsername));
        String normalizedGithubUsername = profile.login().toLowerCase(Locale.ROOT);

        var customerWithEmail = customerGateway.findByEmail(normalizedEmail);
        if (customerWithEmail.isPresent() && !customerWithEmail.get().getId().equals(id)) {
            throw new DuplicateEmailException(normalizedEmail);
        }

        var customerWithGithub = customerGateway.findByGithubUsername(normalizedGithubUsername);
        if (customerWithGithub.isPresent() && !customerWithGithub.get().getId().equals(id)) {
            throw new DuplicateGithubUsernameException(normalizedGithubUsername);
        }

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
