package com.btoapanta.account.service.domain.model;

import com.btoapanta.account.service.domain.enums.MovementType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Movement - Unit Tests")
class MovementTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should create a valid movement")
    void shouldCreateValidMovement() {
        Movement movement = Movement.builder()
                .id(UUID.randomUUID())
                .accountNumber(123456L)
                .date(LocalDateTime.now())
                .movementType(MovementType.CREDIT)
                .amount(BigDecimal.valueOf(500))
                .balanceBefore(BigDecimal.valueOf(1000))
                .balanceAfter(BigDecimal.valueOf(1500))
                .build();

        Set<ConstraintViolation<Movement>> violations = validator.validate(movement);
        assertTrue(violations.isEmpty());
        assertEquals(MovementType.CREDIT, movement.getMovementType());
        assertEquals(BigDecimal.valueOf(500), movement.getAmount());
    }

    @Test
    @DisplayName("Should validate that account number is not null")
    void shouldValidateAccountNumberNotNull() {
        Movement movement = Movement.builder()
                .accountNumber(null)
                .date(LocalDateTime.now())
                .movementType(MovementType.CREDIT)
                .amount(BigDecimal.valueOf(500))
                .balanceBefore(BigDecimal.valueOf(1000))
                .balanceAfter(BigDecimal.valueOf(1500))
                .build();

        Set<ConstraintViolation<Movement>> violations = validator.validate(movement);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Account number is required")));
    }

    @Test
    @DisplayName("Should validate that account number is at least 100000")
    void shouldValidateAccountNumberMinimum() {
        Movement movement = Movement.builder()
                .accountNumber(99999L)
                .date(LocalDateTime.now())
                .movementType(MovementType.CREDIT)
                .amount(BigDecimal.valueOf(500))
                .balanceBefore(BigDecimal.valueOf(1000))
                .balanceAfter(BigDecimal.valueOf(1500))
                .build();

        Set<ConstraintViolation<Movement>> violations = validator.validate(movement);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("at least 100000")));
    }

    @Test
    @DisplayName("Should validate that account number cannot exceed 9999999999")
    void shouldValidateAccountNumberMaximum() {
        Movement movement = Movement.builder()
                .accountNumber(10000000000L)
                .date(LocalDateTime.now())
                .movementType(MovementType.CREDIT)
                .amount(BigDecimal.valueOf(500))
                .balanceBefore(BigDecimal.valueOf(1000))
                .balanceAfter(BigDecimal.valueOf(1500))
                .build();

        Set<ConstraintViolation<Movement>> violations = validator.validate(movement);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("cannot exceed 9999999999")));
    }

    @Test
    @DisplayName("Should validate that date is not null")
    void shouldValidateDateNotNull() {
        Movement movement = Movement.builder()
                .accountNumber(123456L)
                .date(null)
                .movementType(MovementType.CREDIT)
                .amount(BigDecimal.valueOf(500))
                .balanceBefore(BigDecimal.valueOf(1000))
                .balanceAfter(BigDecimal.valueOf(1500))
                .build();

        Set<ConstraintViolation<Movement>> violations = validator.validate(movement);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Movement date is required")));
    }

    @Test
    @DisplayName("Should validate that date is not in the future")
    void shouldValidateDateNotInFuture() {
        Movement movement = Movement.builder()
                .accountNumber(123456L)
                .date(LocalDateTime.now().plusDays(1))
                .movementType(MovementType.CREDIT)
                .amount(BigDecimal.valueOf(500))
                .balanceBefore(BigDecimal.valueOf(1000))
                .balanceAfter(BigDecimal.valueOf(1500))
                .build();

        Set<ConstraintViolation<Movement>> violations = validator.validate(movement);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("cannot be in the future")));
    }

    @Test
    @DisplayName("Should validate that movement type is not null")
    void shouldValidateMovementTypeNotNull() {
        Movement movement = Movement.builder()
                .accountNumber(123456L)
                .date(LocalDateTime.now())
                .movementType(null)
                .amount(BigDecimal.valueOf(500))
                .balanceBefore(BigDecimal.valueOf(1000))
                .balanceAfter(BigDecimal.valueOf(1500))
                .build();

        Set<ConstraintViolation<Movement>> violations = validator.validate(movement);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Movement type is required")));
    }

    @Test
    @DisplayName("Should validate that amount is not null")
    void shouldValidateAmountNotNull() {
        Movement movement = Movement.builder()
                .accountNumber(123456L)
                .date(LocalDateTime.now())
                .movementType(MovementType.CREDIT)
                .amount(null)
                .balanceBefore(BigDecimal.valueOf(1000))
                .balanceAfter(BigDecimal.valueOf(1500))
                .build();

        Set<ConstraintViolation<Movement>> violations = validator.validate(movement);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Amount is required")));
    }

    @Test
    @DisplayName("Should validate that amount must be positive")
    void shouldValidateAmountMustBePositive() {
        Movement movement = Movement.builder()
                .accountNumber(123456L)
                .date(LocalDateTime.now())
                .movementType(MovementType.CREDIT)
                .amount(BigDecimal.ZERO)
                .balanceBefore(BigDecimal.valueOf(1000))
                .balanceAfter(BigDecimal.valueOf(1500))
                .build();

        Set<ConstraintViolation<Movement>> violations = validator.validate(movement);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("must be greater than 0")));
    }

    @Test
    @DisplayName("Should validate that balance before is not null")
    void shouldValidateBalanceBeforeNotNull() {
        Movement movement = Movement.builder()
                .accountNumber(123456L)
                .date(LocalDateTime.now())
                .movementType(MovementType.CREDIT)
                .amount(BigDecimal.valueOf(500))
                .balanceBefore(null)
                .balanceAfter(BigDecimal.valueOf(1500))
                .build();

        Set<ConstraintViolation<Movement>> violations = validator.validate(movement);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Balance before is required")));
    }

    @Test
    @DisplayName("Should validate that balance before cannot be negative")
    void shouldValidateBalanceBeforeCannotBeNegative() {
        Movement movement = Movement.builder()
                .accountNumber(123456L)
                .date(LocalDateTime.now())
                .movementType(MovementType.CREDIT)
                .amount(BigDecimal.valueOf(500))
                .balanceBefore(BigDecimal.valueOf(-100))
                .balanceAfter(BigDecimal.valueOf(400))
                .build();

        Set<ConstraintViolation<Movement>> violations = validator.validate(movement);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("cannot be negative")));
    }

    @Test
    @DisplayName("Should validate that balance after is not null")
    void shouldValidateBalanceAfterNotNull() {
        Movement movement = Movement.builder()
                .accountNumber(123456L)
                .date(LocalDateTime.now())
                .movementType(MovementType.CREDIT)
                .amount(BigDecimal.valueOf(500))
                .balanceBefore(BigDecimal.valueOf(1000))
                .balanceAfter(null)
                .build();

        Set<ConstraintViolation<Movement>> violations = validator.validate(movement);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Balance after is required")));
    }

    @Test
    @DisplayName("Should validate that balance after cannot be negative")
    void shouldValidateBalanceAfterCannotBeNegative() {
        Movement movement = Movement.builder()
                .accountNumber(123456L)
                .date(LocalDateTime.now())
                .movementType(MovementType.DEBIT)
                .amount(BigDecimal.valueOf(500))
                .balanceBefore(BigDecimal.valueOf(400))
                .balanceAfter(BigDecimal.valueOf(-100))
                .build();

        Set<ConstraintViolation<Movement>> violations = validator.validate(movement);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("cannot be negative")));
    }

    @Test
    @DisplayName("Should create DEBIT movement")
    void shouldCreateDebitMovement() {
        Movement movement = Movement.builder()
                .accountNumber(123456L)
                .date(LocalDateTime.now())
                .movementType(MovementType.DEBIT)
                .amount(BigDecimal.valueOf(300))
                .balanceBefore(BigDecimal.valueOf(1000))
                .balanceAfter(BigDecimal.valueOf(700))
                .build();

        Set<ConstraintViolation<Movement>> violations = validator.validate(movement);
        assertTrue(violations.isEmpty());
        assertEquals(MovementType.DEBIT, movement.getMovementType());
    }

    @Test
    @DisplayName("Should accept balance before at zero")
    void shouldAcceptBalanceBeforeAtZero() {
        Movement movement = Movement.builder()
                .accountNumber(123456L)
                .date(LocalDateTime.now())
                .movementType(MovementType.CREDIT)
                .amount(BigDecimal.valueOf(500))
                .balanceBefore(BigDecimal.ZERO)
                .balanceAfter(BigDecimal.valueOf(500))
                .build();

        Set<ConstraintViolation<Movement>> violations = validator.validate(movement);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept balance after at zero")
    void shouldAcceptBalanceAfterAtZero() {
        Movement movement = Movement.builder()
                .accountNumber(123456L)
                .date(LocalDateTime.now())
                .movementType(MovementType.DEBIT)
                .amount(BigDecimal.valueOf(500))
                .balanceBefore(BigDecimal.valueOf(500))
                .balanceAfter(BigDecimal.ZERO)
                .build();

        Set<ConstraintViolation<Movement>> violations = validator.validate(movement);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept date at present moment")
    void shouldAcceptDateAtPresentMoment() {
        Movement movement = Movement.builder()
                .accountNumber(123456L)
                .date(LocalDateTime.now())
                .movementType(MovementType.CREDIT)
                .amount(BigDecimal.valueOf(500))
                .balanceBefore(BigDecimal.valueOf(1000))
                .balanceAfter(BigDecimal.valueOf(1500))
                .build();

        Set<ConstraintViolation<Movement>> violations = validator.validate(movement);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept account number at minimum boundary")
    void shouldAcceptAccountNumberAtMinimumBoundary() {
        Movement movement = Movement.builder()
                .accountNumber(100000L)
                .date(LocalDateTime.now())
                .movementType(MovementType.CREDIT)
                .amount(BigDecimal.valueOf(500))
                .balanceBefore(BigDecimal.valueOf(1000))
                .balanceAfter(BigDecimal.valueOf(1500))
                .build();

        Set<ConstraintViolation<Movement>> violations = validator.validate(movement);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept account number at maximum boundary")
    void shouldAcceptAccountNumberAtMaximumBoundary() {
        Movement movement = Movement.builder()
                .accountNumber(9999999999L)
                .date(LocalDateTime.now())
                .movementType(MovementType.CREDIT)
                .amount(BigDecimal.valueOf(500))
                .balanceBefore(BigDecimal.valueOf(1000))
                .balanceAfter(BigDecimal.valueOf(1500))
                .build();

        Set<ConstraintViolation<Movement>> violations = validator.validate(movement);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should validate positive amount edge case")
    void shouldValidatePositiveAmountEdgeCase() {
        Movement movement = Movement.builder()
                .accountNumber(123456L)
                .date(LocalDateTime.now())
                .movementType(MovementType.CREDIT)
                .amount(BigDecimal.valueOf(0.01))
                .balanceBefore(BigDecimal.valueOf(1000))
                .balanceAfter(BigDecimal.valueOf(1000.01))
                .build();

        Set<ConstraintViolation<Movement>> violations = validator.validate(movement);
        assertTrue(violations.isEmpty());
    }
}
