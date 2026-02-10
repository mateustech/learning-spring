package main.customer.usecases;

import main.customer.domain.model.Customer;
import main.customer.infrastructure.persistence.CustomerJpaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListCustomersUseCase {

    private final CustomerJpaRepository customerRepository;

    public ListCustomersUseCase(CustomerJpaRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public List<Customer> execute(boolean activeOnly) {
        return activeOnly ? customerRepository.findByActiveTrue() : customerRepository.findAll();
    }
}
