package com.brigeth.infraestructure.adapter.input.rest.exception;

import com.brigeth.customer.infrastructure.adapter.input.rest.model.ErrorResponse;
import com.brigeth.domain.exception.BusinessRuleException;
import com.brigeth.domain.exception.CustomerNotFoundException;
import com.brigeth.domain.exception.DomainException;
import com.brigeth.domain.exception.DuplicateIdentificationException;
import com.brigeth.domain.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler - Unit Tests")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private org.springframework.http.server.reactive.ServerHttpRequest request;

    @Mock
    private org.springframework.http.server.RequestPath requestPath;

    @BeforeEach
    void setUp() {
        when(exchange.getRequest()).thenReturn(request);
        when(request.getPath()).thenReturn(requestPath);
        when(requestPath.value()).thenReturn("/customers");
    }

    @Test
    @DisplayName("Should handle ValidationException with 400 status")
    void shouldHandleValidationException() {
        ValidationException exception = new ValidationException("Validation error message");

        StepVerifier.create(globalExceptionHandler.handleValidationException(exception, exchange))
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                    ErrorResponse error = response.getBody();
                    assertNotNull(error);
                    assertEquals(400, error.getStatus());
                    assertEquals("VALIDATION_ERROR", error.getError());
                    assertEquals("Validation error message", error.getMessage());
                    assertEquals("/customers", error.getPath());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle CustomerNotFoundException with 404 status")
    void shouldHandleCustomerNotFoundException() {
        CustomerNotFoundException exception = new CustomerNotFoundException("123");

        StepVerifier.create(globalExceptionHandler.handleCustomerNotFoundException(exception, exchange))
                .assertNext(response -> {
                    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                    ErrorResponse error = response.getBody();
                    assertNotNull(error);
                    assertEquals(404, error.getStatus());
                    assertEquals("NOT_FOUND", error.getError());
                    assertTrue(error.getMessage().contains("123"));
                    assertEquals("/customers", error.getPath());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle DuplicateIdentificationException with 409 status")
    void shouldHandleDuplicateIdentificationException() {
        DuplicateIdentificationException exception = new DuplicateIdentificationException("1234567890");

        StepVerifier.create(globalExceptionHandler.handleDuplicateIdentificationException(exception, exchange))
                .assertNext(response -> {
                    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
                    ErrorResponse error = response.getBody();
                    assertNotNull(error);
                    assertEquals(409, error.getStatus());
                    assertEquals("CONFLICT", error.getError());
                    assertTrue(error.getMessage().contains("1234567890"));
                    assertEquals("/customers", error.getPath());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle BusinessRuleException with 422 status")
    void shouldHandleBusinessRuleException() {
        BusinessRuleException exception = new BusinessRuleException("Business rule violated");

        StepVerifier.create(globalExceptionHandler.handleBusinessRuleException(exception, exchange))
                .assertNext(response -> {
                    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
                    ErrorResponse error = response.getBody();
                    assertNotNull(error);
                    assertEquals(422, error.getStatus());
                    assertEquals("BUSINESS_RULE_VIOLATION", error.getError());
                    assertEquals("Business rule violated", error.getMessage());
                    assertEquals("/customers", error.getPath());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle WebExchangeBindException with 400 status")
    void shouldHandleWebExchangeBindException() {
        BindingResult bindingResult = org.mockito.Mockito.mock(BindingResult.class);
        WebExchangeBindException exception = new WebExchangeBindException(null, bindingResult);
        
        List<FieldError> fieldErrors = Arrays.asList(
                new FieldError("customer", "name", "Name is required"),
                new FieldError("customer", "email", "Email is invalid")
        );
        
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        StepVerifier.create(globalExceptionHandler.handleWebExchangeBindException(exception, exchange))
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                    ErrorResponse error = response.getBody();
                    assertNotNull(error);
                    assertEquals(400, error.getStatus());
                    assertEquals("VALIDATION_ERROR", error.getError());
                    assertTrue(error.getMessage().contains("name"));
                    assertTrue(error.getMessage().contains("email"));
                    assertEquals("/customers", error.getPath());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException with 400 status")
    void shouldHandleIllegalArgumentException() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        StepVerifier.create(globalExceptionHandler.handleIllegalArgumentException(exception, exchange))
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                    ErrorResponse error = response.getBody();
                    assertNotNull(error);
                    assertEquals(400, error.getStatus());
                    assertEquals("BAD_REQUEST", error.getError());
                    assertEquals("Invalid argument", error.getMessage());
                    assertEquals("/customers", error.getPath());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle DomainException with 400 status")
    void shouldHandleDomainException() {
        DomainException exception = new DomainException("Domain error");

        StepVerifier.create(globalExceptionHandler.handleDomainException(exception, exchange))
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                    ErrorResponse error = response.getBody();
                    assertNotNull(error);
                    assertEquals(400, error.getStatus());
                    assertEquals("DOMAIN_ERROR", error.getError());
                    assertEquals("Domain error", error.getMessage());
                    assertEquals("/customers", error.getPath());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle generic Exception with 500 status")
    void shouldHandleGenericException() {
        Exception exception = new Exception("Unexpected error");

        StepVerifier.create(globalExceptionHandler.handleGenericException(exception, exchange))
                .assertNext(response -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
                    ErrorResponse error = response.getBody();
                    assertNotNull(error);
                    assertEquals(500, error.getStatus());
                    assertEquals("INTERNAL_SERVER_ERROR", error.getError());
                    assertEquals("An internal server error has occurred", error.getMessage());
                    assertEquals("/customers", error.getPath());
                })
                .verifyComplete();
    }
}
