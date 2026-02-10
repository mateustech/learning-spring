package learning.customer.application.usecase;

import learning.customer.domain.model.Customer;
import learning.customer.infrastructure.persistence.CustomerJpaRepository;
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
