package learning.customer.usecases;

import learning.customer.domain.model.Customer;
import learning.customer.domain.exception.CustomerNotFoundException;
import learning.customer.infrastructure.persistence.CustomerJpaRepository;
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
