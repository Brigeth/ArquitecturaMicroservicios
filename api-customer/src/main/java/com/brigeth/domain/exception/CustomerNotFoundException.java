package com.brigeth.domain.exception;

public class CustomerNotFoundException extends DomainException {
    
    public CustomerNotFoundException(String customerId) {
        super("Customer not found: " + customerId);
    }
}
