package com.example.demo.customer.application.usecase;

import com.example.demo.customer.Customer;
import com.example.demo.customer.CustomerNotFoundException;
import com.example.demo.customer.application.port.CustomerGateway;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteCustomerUseCase {

    private final CustomerGateway customerGateway;

    public DeleteCustomerUseCase(CustomerGateway customerGateway) {
        this.customerGateway = customerGateway;
    }

    @Transactional
    public void execute(Long id) {
        Customer customer = customerGateway.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
        customerGateway.delete(customer);
    }
}
