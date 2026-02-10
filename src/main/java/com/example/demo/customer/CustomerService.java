package com.example.demo.customer;

import com.example.demo.integration.github.GitHubClient;
import com.example.demo.integration.github.GitHubProfile;
import java.util.Locale;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final GitHubClient gitHubClient;

    public CustomerService(CustomerRepository customerRepository, GitHubClient gitHubClient) {
        this.customerRepository = customerRepository;
        this.gitHubClient = gitHubClient;
    }

    @Transactional
    public Customer create(String email, String githubUsername) {
        String normalizedEmail = normalizeEmail(email);
        GitHubProfile profile = gitHubClient.fetchProfile(normalizeGithubUsername(githubUsername));
        String normalizedGithubUsername = profile.login().toLowerCase(Locale.ROOT);

        if (customerRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateEmailException(normalizedEmail);
        }
        if (customerRepository.existsByGithubUsername(normalizedGithubUsername)) {
            throw new DuplicateGithubUsernameException(normalizedGithubUsername);
        }

        var customer = new Customer();
        customer.setName(profile.displayName());
        customer.setEmail(normalizedEmail);
        customer.setGithubUsername(normalizedGithubUsername);
        return customerRepository.save(customer);
    }

    @Transactional(readOnly = true)
    public List<Customer> listAll() {
        return customerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Customer> listActive() {
        return customerRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public Customer getById(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @Transactional
    public Customer update(Long id, String email, String githubUsername) {
        Customer customer = getById(id);
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

    @Transactional
    public Customer deactivate(Long id) {
        var customer = getById(id);
        customer.deactivate();
        return customerRepository.save(customer);
    }

    @Transactional
    public Customer activate(Long id) {
        var customer = getById(id);
        customer.activate();
        return customerRepository.save(customer);
    }

    @Transactional
    public void delete(Long id) {
        var customer = getById(id);
        customerRepository.delete(customer);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private String normalizeGithubUsername(String githubUsername) {
        return githubUsername.trim();
    }
}
