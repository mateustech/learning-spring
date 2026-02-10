package com.example.demo.customer.infra.persistence;

import com.example.demo.customer.Customer;
import com.example.demo.customer.CustomerRepository;
import com.example.demo.customer.application.port.CustomerGateway;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class CustomerGatewayAdapter implements CustomerGateway {

    private final CustomerRepository customerRepository;

    public CustomerGatewayAdapter(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public List<Customer> findActive() {
        return customerRepository.findByActiveTrue();
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    @Override
    public Optional<Customer> findByGithubUsername(String githubUsername) {
        return customerRepository.findByGithubUsername(githubUsername);
    }

    @Override
    public boolean existsByEmail(String email) {
        return customerRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByGithubUsername(String githubUsername) {
        return customerRepository.existsByGithubUsername(githubUsername);
    }

    @Override
    public void delete(Customer customer) {
        customerRepository.delete(customer);
    }
}
