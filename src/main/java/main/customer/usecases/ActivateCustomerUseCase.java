package main.customer.usecases;

import main.customer.domain.model.Customer;
import main.customer.domain.exception.CustomerNotFoundException;
import main.customer.infrastructure.persistence.CustomerJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivateCustomerUseCase {

    private final CustomerJpaRepository customerRepository;

    public ActivateCustomerUseCase(CustomerJpaRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Customer execute(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
        customer.activate();
        return customerRepository.save(customer);
    }
}
