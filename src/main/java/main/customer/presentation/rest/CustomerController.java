package main.customer.presentation.rest;

import main.customer.presentation.rest.dto.CustomerRequest;
import main.customer.presentation.rest.dto.CustomerResponse;
import main.customer.usecases.ActivateCustomerUseCase;
import main.customer.usecases.CreateCustomerUseCase;
import main.customer.usecases.DeactivateCustomerUseCase;
import main.customer.usecases.DeleteCustomerUseCase;
import main.customer.usecases.GetCustomerByIdUseCase;
import main.customer.usecases.ListCustomersUseCase;
import main.customer.usecases.UpdateCustomerUseCase;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

    private final CreateCustomerUseCase createCustomerUseCase;
    private final ListCustomersUseCase listCustomersUseCase;
    private final GetCustomerByIdUseCase getCustomerByIdUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final ActivateCustomerUseCase activateCustomerUseCase;
    private final DeactivateCustomerUseCase deactivateCustomerUseCase;
    private final DeleteCustomerUseCase deleteCustomerUseCase;

    public CustomerController(
        CreateCustomerUseCase createCustomerUseCase,
        ListCustomersUseCase listCustomersUseCase,
        GetCustomerByIdUseCase getCustomerByIdUseCase,
        UpdateCustomerUseCase updateCustomerUseCase,
        ActivateCustomerUseCase activateCustomerUseCase,
        DeactivateCustomerUseCase deactivateCustomerUseCase,
        DeleteCustomerUseCase deleteCustomerUseCase
    ) {
        this.createCustomerUseCase = createCustomerUseCase;
        this.listCustomersUseCase = listCustomersUseCase;
        this.getCustomerByIdUseCase = getCustomerByIdUseCase;
        this.updateCustomerUseCase = updateCustomerUseCase;
        this.activateCustomerUseCase = activateCustomerUseCase;
        this.deactivateCustomerUseCase = deactivateCustomerUseCase;
        this.deleteCustomerUseCase = deleteCustomerUseCase;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        log.info(
            "event=http_customer_create_received correlationId={} email={} githubUsername={}",
            MDC.get("correlationId"),
            request.email(),
            request.githubUsername()
        );
        var customer = createCustomerUseCase.execute(request.email(), request.githubUsername());
        var location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(customer.getId())
            .toUri();
        return ResponseEntity.created(location).body(CustomerResponse.from(customer));
    }

    @GetMapping
    public List<CustomerResponse> list(@RequestParam(defaultValue = "false") boolean activeOnly) {
        log.info(
            "event=http_customer_list_received correlationId={} activeOnly={}",
            MDC.get("correlationId"),
            activeOnly
        );
        var customers = listCustomersUseCase.execute(activeOnly);
        return customers.stream().map(CustomerResponse::from).toList();
    }

    @GetMapping("/{id}")
    public CustomerResponse getById(@PathVariable Long id) {
        return CustomerResponse.from(getCustomerByIdUseCase.execute(id));
    }

    @PutMapping("/{id}")
    public CustomerResponse update(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
        var customer = updateCustomerUseCase.execute(id, request.email(), request.githubUsername());
        return CustomerResponse.from(customer);
    }

    @PatchMapping("/{id}/activate")
    public CustomerResponse activate(@PathVariable Long id) {
        return CustomerResponse.from(activateCustomerUseCase.execute(id));
    }

    @PatchMapping("/{id}/deactivate")
    public CustomerResponse deactivate(@PathVariable Long id) {
        return CustomerResponse.from(deactivateCustomerUseCase.execute(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        deleteCustomerUseCase.execute(id);
    }
}
