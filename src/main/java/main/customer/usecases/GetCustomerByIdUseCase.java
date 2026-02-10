package main.customer.usecases;

import main.customer.domain.model.Customer;
import main.customer.domain.exception.CustomerNotFoundException;
import main.customer.infrastructure.persistence.CustomerJpaRepository;
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
