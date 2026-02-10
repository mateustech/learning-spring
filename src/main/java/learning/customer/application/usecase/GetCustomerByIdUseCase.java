package learning.customer.application.usecase;

import learning.customer.domain.model.Customer;
import learning.customer.domain.exception.CustomerNotFoundException;
import learning.customer.infrastructure.persistence.CustomerJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetCustomerByIdUseCase {

    private final CustomerJpaRepository customerRepository;

    public GetCustomerByIdUseCase(CustomerJpaRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public Customer execute(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
    }
}
