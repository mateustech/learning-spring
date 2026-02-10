package main.customer.usecases;

import main.customer.domain.model.Customer;
import main.customer.domain.exception.CustomerNotFoundException;
import main.customer.infrastructure.persistence.CustomerJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeactivateCustomerUseCase {

    private final CustomerJpaRepository customerRepository;

    public DeactivateCustomerUseCase(CustomerJpaRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Customer execute(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
        customer.deactivate();
        return customerRepository.save(customer);
    }
}
