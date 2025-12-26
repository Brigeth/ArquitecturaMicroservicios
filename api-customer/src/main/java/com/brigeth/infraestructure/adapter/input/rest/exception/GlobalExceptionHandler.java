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

/**
 * Manejador global de excepciones para el adaptador REST
 * Alineado con los códigos HTTP definidos en openapi.yaml
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * HTTP 400 - Errores de validación de campos
     * openapi.yaml: '400' - Datos de entrada inválidos
     */
    @ExceptionHandler(ValidationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidationException(
            ValidationException ex, 
            ServerWebExchange exchange) {
        log.error("Error de validación de dominio: {}", ex.getMessage());
        
        ErrorResponse error = buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "VALIDATION_ERROR",
            ex.getMessage(),
            exchange.getRequest().getPath().value()
        );
        
        return Mono.just(ResponseEntity.badRequest().body(error));
    }

    /**
     * HTTP 404 - Cliente no encontrado
     * openapi.yaml: '404' - Cliente no encontrado / Recurso inexistente
     */
    @ExceptionHandler(CustomerNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleCustomerNotFoundException(
            CustomerNotFoundException ex, 
            ServerWebExchange exchange) {
        log.error("Cliente no encontrado: {}", ex.getMessage());
        
        ErrorResponse error = buildErrorResponse(
            HttpStatus.NOT_FOUND,
            "NOT_FOUND",
            ex.getMessage(),
            exchange.getRequest().getPath().value()
        );
        
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(error));
    }

    /**
     * HTTP 409 - Conflicto (identificación duplicada)
     * openapi.yaml: '409' - El cliente ya existe / Identificación duplicada
     */
    @ExceptionHandler(DuplicateIdentificationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleDuplicateIdentificationException(
            DuplicateIdentificationException ex, 
            ServerWebExchange exchange) {
        log.error("Identificación duplicada: {}", ex.getMessage());
        
        ErrorResponse error = buildErrorResponse(
            HttpStatus.CONFLICT,
            "CONFLICT",
            ex.getMessage(),
            exchange.getRequest().getPath().value()
        );
        
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(error));
    }

    /**
     * HTTP 422 - Reglas de negocio no cumplidas
     * openapi.yaml: '422' - Reglas de negocio / Cliente menor de edad
     */
    @ExceptionHandler(BusinessRuleException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleBusinessRuleException(
            BusinessRuleException ex, 
            ServerWebExchange exchange) {
        log.error("Regla de negocio no cumplida: {}", ex.getMessage());
        
        ErrorResponse error = buildErrorResponse(
            HttpStatus.UNPROCESSABLE_ENTITY,
            "BUSINESS_RULE_VIOLATION",
            ex.getMessage(),
            exchange.getRequest().getPath().value()
        );
        
        return Mono.just(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error));
    }

    /**
     * HTTP 400 - Errores de validación de entrada (Bean Validation)
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
        
        log.error("Errores de validación de entrada: {}", errors);
        
        ErrorResponse error = buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "VALIDATION_ERROR",
            "Errores de validación: " + errors,
            exchange.getRequest().getPath().value()
        );
        
        return Mono.just(ResponseEntity.badRequest().body(error));
    }

    /**
     * HTTP 400 - Argumentos inválidos
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleIllegalArgumentException(
            IllegalArgumentException ex, 
            ServerWebExchange exchange) {
        log.error("Argumento inválido: {}", ex.getMessage());
        
        ErrorResponse error = buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "BAD_REQUEST",
            ex.getMessage(),
            exchange.getRequest().getPath().value()
        );
        
        return Mono.just(ResponseEntity.badRequest().body(error));
    }

    /**
     * HTTP 400 - Otras excepciones de dominio
     */
    @ExceptionHandler(DomainException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleDomainException(
            DomainException ex, 
            ServerWebExchange exchange) {
        log.error("Error de dominio: {}", ex.getMessage());
        
        ErrorResponse error = buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "DOMAIN_ERROR",
            ex.getMessage(),
            exchange.getRequest().getPath().value()
        );
        
        return Mono.just(ResponseEntity.badRequest().body(error));
    }

    /**
     * HTTP 500 - Error interno del servidor
     * openapi.yaml: '500' - Error interno del servidor
     */
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGenericException(
            Exception ex, 
            ServerWebExchange exchange) {
        log.error("Error interno del servidor: {}", ex.getMessage(), ex);
        
        ErrorResponse error = buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "INTERNAL_SERVER_ERROR",
            "Ha ocurrido un error interno en el servidor",
            exchange.getRequest().getPath().value()
        );
        
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
    }

    /**
     * Construye una respuesta de error estándar según openapi.yaml
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
