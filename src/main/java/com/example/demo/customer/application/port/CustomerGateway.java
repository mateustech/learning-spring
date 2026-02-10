package com.example.demo.customer.application.port;

import com.example.demo.customer.Customer;
import java.util.List;
import java.util.Optional;

public interface CustomerGateway {

    Customer save(Customer customer);

    List<Customer> findAll();

    List<Customer> findActive();

    Optional<Customer> findById(Long id);

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByGithubUsername(String githubUsername);

    boolean existsByEmail(String email);

    boolean existsByGithubUsername(String githubUsername);

    void delete(Customer customer);
}
