package com.brigeth.application.service.impl;


import com.brigeth.application.service.CustomerService;
import com.brigeth.application.service.ValidationService;
import com.brigeth.domain.exception.ValidationException;
import com.brigeth.domain.models.Customer;
import com.brigeth.domain.port.output.CustomerPersistencePort;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerPersistencePort customerPersistencePort;
    private final ValidationService validationService;
    private final Validator validator;

    @Override
    public Mono<Customer> createCustomer(Customer customer) {
        log.info("Creating a client with identification: {}", customer.getIdentification());
        customer.normalize();
        validateCustomer(customer);
        return validationService.validateUniqueIdentification(customer.getIdentification(), null)
            .then(customerPersistencePort.saveCustomer(customer))
            .doOnSuccess(c -> log.info("Client created successfully: {}", c.getPersonId()))
            .doOnError(e -> log.error("Error creating client: {}", e.getMessage()));
    }

    @Override
    public Flux<Customer> getCustomers() {
        log.debug("Getting all the customers");
        return customerPersistencePort.getAllCustomers();
    }

    @Override
    public Mono<Customer> getOnlyCustomerById(String customerId) {
        log.info("Searching for customer: {}", customerId);
        return customerPersistencePort.getCustomerById(customerId)
                .doOnSuccess(c -> log.debug("Customer found: {}", customerId))
                .doOnError(e -> log.warn("Customer not found: {}", customerId));
    }

    @Override
    public Mono<Void> deleteCustomer(String customerId) {
        log.info("Deleting customer: {}", customerId);
        
        // Validate that the customer exists before deleting
        return validationService.validateCustomerExists(customerId)
            .then(customerPersistencePort.deleteCustomer(customerId))
            .doOnSuccess(v -> log.info("Customer deleted successfully: {}", customerId))
            .doOnError(e -> log.error("Error deleting customer: {}", e.getMessage()));
    }

    @Override
    public Mono<Customer> updateCustomer(Customer customer) {
        log.info("Updating customer: {}", customer.getPersonId());
        
        customer.normalize();
        validatePerson(customer);
        
        // Validate that the customer exists and that the identification is unique
        return validationService.validateCustomerExists(customer.getPersonId().toString())
            .then(validationService.validateUniqueIdentification(
                customer.getIdentification(), 
                customer.getPersonId().toString()
            ))
            .then(customerPersistencePort.updateCustomer(customer))
            .doOnSuccess(c -> log.info("Customer updated successfully: {}", c.getPersonId()))
            .doOnError(e -> log.error("Error updating customer: {}", e.getMessage()));
    }
    
    private void validateCustomer(Customer customer) {
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        if (!violations.isEmpty()) {
            String errors = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
            throw new ValidationException(errors);
        }
    }
    
    private void validatePerson(Customer customer) {
        // Only validate Person fields (password not required in updates)
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        Set<ConstraintViolation<Customer>> personViolations = violations.stream()
            .filter(v -> !v.getPropertyPath().toString().equals("password"))
            .collect(Collectors.toSet());
        
        if (!personViolations.isEmpty()) {
            String errors = personViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
            throw new ValidationException(errors);
        }
    }
}
