package com.example.demo.customer.application.usecase;

import com.example.demo.customer.Customer;
import com.example.demo.customer.application.port.CustomerGateway;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListCustomersUseCase {

    private final CustomerGateway customerGateway;

    public ListCustomersUseCase(CustomerGateway customerGateway) {
        this.customerGateway = customerGateway;
    }

    @Transactional(readOnly = true)
    public List<Customer> execute(boolean activeOnly) {
        return activeOnly ? customerGateway.findActive() : customerGateway.findAll();
    }
}
