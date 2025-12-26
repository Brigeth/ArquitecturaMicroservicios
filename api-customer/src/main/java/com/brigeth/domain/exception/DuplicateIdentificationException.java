package com.brigeth.domain.exception;

public class DuplicateIdentificationException extends DomainException {
    
    public DuplicateIdentificationException(String identification) {
        super("Ya existe un cliente con la identificación: " + identification);
    }
}
