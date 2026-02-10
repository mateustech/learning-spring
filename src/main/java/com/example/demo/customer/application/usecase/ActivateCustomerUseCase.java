package com.example.demo.customer.application.usecase;

import com.example.demo.customer.Customer;
import com.example.demo.customer.CustomerNotFoundException;
import com.example.demo.customer.application.port.CustomerGateway;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivateCustomerUseCase {

    private final CustomerGateway customerGateway;

    public ActivateCustomerUseCase(CustomerGateway customerGateway) {
        this.customerGateway = customerGateway;
    }

    @Transactional
    public Customer execute(Long id) {
        Customer customer = customerGateway.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
        customer.activate();
        return customerGateway.save(customer);
    }
}
