package com.btoapanta.account.service.infrastructure.adapter.input.rest.exception;

import com.btoapanta.account.service.domain.exception.notfound.AccountNotFoundException;
import com.btoapanta.account.service.domain.exception.DuplicateAccountException;
import com.btoapanta.account.service.domain.exception.business.InvalidBalanceException;
import com.btoapanta.account.service.domain.exception.InvalidAccountStateException;
import com.btoapanta.account.service.infrastructure.input.adapter.rest.account.service.models.ErrorResponse;
import com.btoapanta.account.service.infrastructure.input.adapter.rest.account.service.models.ValidationError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleAccountNotFound(
            AccountNotFoundException ex,
            ServerWebExchange exchange) {

        log.error("Account not found: {}", ex.getMessage());

        ErrorResponse errorResponse = buildErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );

        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse));
    }

    @ExceptionHandler(DuplicateAccountException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleDuplicateAccount(
            DuplicateAccountException ex,
            ServerWebExchange exchange) {

        log.error("Duplicate account: {}", ex.getMessage());

        ErrorResponse errorResponse = buildErrorResponse(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );

        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse));
    }

    @ExceptionHandler(InvalidBalanceException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInsufficientBalance(
            InvalidBalanceException ex,
            ServerWebExchange exchange) {

        log.error("Insufficient balance: {}", ex.getMessage());

        ErrorResponse errorResponse = buildErrorResponse(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );

        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse));
    }

    @ExceptionHandler(InvalidAccountStateException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInvalidAccountState(
            InvalidAccountStateException ex,
            ServerWebExchange exchange) {

        log.error("Invalid account state: {}", ex.getMessage());

        ErrorResponse errorResponse = buildErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY,
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );

        return Mono.just(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidationException(
            WebExchangeBindException ex,
            ServerWebExchange exchange) {

        log.error("Validation error: {}", ex.getMessage());

        List<ValidationError> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::buildValidationError)
                .collect(Collectors.toList());

        ErrorResponse errorResponse = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                exchange.getRequest().getPath().value()
        );
        errorResponse.setErrors(validationErrors);

        return Mono.just(ResponseEntity.badRequest().body(errorResponse));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGenericException(
            Exception ex,
            ServerWebExchange exchange) {

        log.error("Unexpected error: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                exchange.getRequest().getPath().value()
        );

        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
    }

    private ErrorResponse buildErrorResponse(HttpStatus status, String message, String path) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(OffsetDateTime.now());
        errorResponse.setStatus(status.value());
        errorResponse.setError(status.getReasonPhrase());
        errorResponse.setMessage(message);
        errorResponse.setPath(path);
        errorResponse.setTraceId(UUID.randomUUID());

        return errorResponse;
    }


    private ValidationError buildValidationError(FieldError fieldError) {
        ValidationError validationError = new ValidationError();
        validationError.setField(fieldError.getField());
        validationError.setMessage(fieldError.getDefaultMessage());
        validationError.setRejectedValue(fieldError.getRejectedValue());

        return validationError;
    }
}
