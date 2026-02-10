package learning.customer.application.usecase;

import learning.customer.domain.model.Customer;
import learning.customer.domain.exception.CustomerNotFoundException;
import learning.customer.infrastructure.persistence.CustomerJpaRepository;
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
