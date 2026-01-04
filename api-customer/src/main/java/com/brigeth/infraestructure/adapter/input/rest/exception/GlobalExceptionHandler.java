package com.brigeth.infraestructure.adapter.input.rest.exception;

import com.brigeth.customer.infrastructure.adapter.input.rest.model.ErrorResponse;
import com.brigeth.domain.exception.BusinessRuleException;
import com.brigeth.domain.exception.CustomerNotFoundException;
import com.brigeth.domain.exception.DomainException;
import com.brigeth.domain.exception.DuplicateIdentificationException;
import com.brigeth.domain.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * HTTP 400 - Field validation errors
     */
    @ExceptionHandler(ValidationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidationException(
            ValidationException ex, 
            ServerWebExchange exchange) {
        log.error("Domain validation error: {}", ex.getMessage());
        
        ErrorResponse error = buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "VALIDATION_ERROR",
            ex.getMessage(),
            exchange.getRequest().getPath().value()
        );
        
        return Mono.just(ResponseEntity.badRequest().body(error));
    }

    /**
     * HTTP 404 - Customer not found
     */
    @ExceptionHandler(CustomerNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleCustomerNotFoundException(
            CustomerNotFoundException ex, 
            ServerWebExchange exchange) {
        log.error("Customer not found: {}", ex.getMessage());
        
        ErrorResponse error = buildErrorResponse(
            HttpStatus.NOT_FOUND,
            "NOT_FOUND",
            ex.getMessage(),
            exchange.getRequest().getPath().value()
        );
        
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(error));
    }

    /**
     * HTTP 409 - Conflict (duplicate identification)
     */
    @ExceptionHandler(DuplicateIdentificationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleDuplicateIdentificationException(
            DuplicateIdentificationException ex, 
            ServerWebExchange exchange) {
        log.error("Duplicate identification: {}", ex.getMessage());
        
        ErrorResponse error = buildErrorResponse(
            HttpStatus.CONFLICT,
            "CONFLICT",
            ex.getMessage(),
            exchange.getRequest().getPath().value()
        );
        
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(error));
    }

    /**
     * HTTP 422 - Business rules not followed
     */
    @ExceptionHandler(BusinessRuleException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleBusinessRuleException(
            BusinessRuleException ex, 
            ServerWebExchange exchange) {
        log.error("Business rule not followed: {}", ex.getMessage());
        
        ErrorResponse error = buildErrorResponse(
            HttpStatus.UNPROCESSABLE_ENTITY,
            "BUSINESS_RULE_VIOLATION",
            ex.getMessage(),
            exchange.getRequest().getPath().value()
        );
        
        return Mono.just(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error));
    }

    /**
     * HTTP 400 - Input validation errors (Bean Validation)
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleWebExchangeBindException(
            WebExchangeBindException ex, 
            ServerWebExchange exchange) {
        
        String errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        
        log.error("Input validation errors: {}", errors);
        
        ErrorResponse error = buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "VALIDATION_ERROR",
            "Validation errors: " + errors,
            exchange.getRequest().getPath().value()
        );
        
        return Mono.just(ResponseEntity.badRequest().body(error));
    }

    /**
     * HTTP 400 - Invalid arguments
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleIllegalArgumentException(
            IllegalArgumentException ex, 
            ServerWebExchange exchange) {
        log.error("Invalid argument: {}", ex.getMessage());
        
        ErrorResponse error = buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "BAD_REQUEST",
            ex.getMessage(),
            exchange.getRequest().getPath().value()
        );
        
        return Mono.just(ResponseEntity.badRequest().body(error));
    }

    /**
     * HTTP 400 - Other domain exceptions
     */
    @ExceptionHandler(DomainException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleDomainException(
            DomainException ex, 
            ServerWebExchange exchange) {
        log.error("Domain error: {}", ex.getMessage());
        
        ErrorResponse error = buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "DOMAIN_ERROR",
            ex.getMessage(),
            exchange.getRequest().getPath().value()
        );
        
        return Mono.just(ResponseEntity.badRequest().body(error));
    }

    /**
     * HTTP 500 - Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGenericException(
            Exception ex, 
            ServerWebExchange exchange) {
        log.error("Internal Server Error: {}", ex.getMessage(), ex);
        
        ErrorResponse error = buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "INTERNAL_SERVER_ERROR",
            "An internal server error has occurred",
            exchange.getRequest().getPath().value()
        );
        
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
    }

    /**
     *Build a standard error response according to openapi.yaml
     */
    private ErrorResponse buildErrorResponse(
            HttpStatus status, 
            String errorCode, 
            String message, 
            String path) {
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(OffsetDateTime.now());
        error.setStatus(status.value());
        error.setError(errorCode);
        error.setMessage(message);
        error.setPath(path);
        return error;
    }
}
