package com.brigeth.domain.exception;

/**
 * Excepción para errores de reglas de negocio (HTTP 422)
 */
public class BusinessRuleException extends DomainException {
    
    public BusinessRuleException(String message) {
        super(message);
    }
}
