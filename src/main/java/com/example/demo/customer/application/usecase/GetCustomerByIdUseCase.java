package com.example.demo.customer.application.usecase;

import com.example.demo.customer.Customer;
import com.example.demo.customer.CustomerNotFoundException;
import com.example.demo.customer.application.port.CustomerGateway;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetCustomerByIdUseCase {

    private final CustomerGateway customerGateway;

    public GetCustomerByIdUseCase(CustomerGateway customerGateway) {
        this.customerGateway = customerGateway;
    }

    @Transactional(readOnly = true)
    public Customer execute(Long id) {
        return customerGateway.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
    }
}
