package com.brigeth.domain.exception;

public class DuplicateIdentificationException extends DomainException {
    
    public DuplicateIdentificationException(String identification) {
        super("A customer with the identification already exists: " + identification);
    }
}
