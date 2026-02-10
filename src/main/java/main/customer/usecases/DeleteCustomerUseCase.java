package main.customer.usecases;

import main.customer.domain.model.Customer;
import main.customer.domain.exception.CustomerNotFoundException;
import main.customer.infrastructure.persistence.CustomerJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteCustomerUseCase {

    private final CustomerJpaRepository customerRepository;

    public DeleteCustomerUseCase(CustomerJpaRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public void execute(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
        customerRepository.delete(customer);
    }
}
