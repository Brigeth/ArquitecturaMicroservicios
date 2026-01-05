package com.btoapanta.account.service.domain.model;

import com.btoapanta.account.service.domain.enums.AccountType;
import com.btoapanta.account.service.domain.enums.MovementType;
import com.btoapanta.account.service.domain.exception.business.InvalidBalanceException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Account - Unit Tests")
class AccountTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should create a valid account")
    void shouldCreateValidAccount() {
        Account account = Account.builder()
                .id(UUID.randomUUID())
                .accountNumber(123456L)
                .customerId(UUID.randomUUID())
                .customerName("Juan Perez")
                .accountType(AccountType.SAVINGS)
                .balance(BigDecimal.valueOf(1000))
                .state(true)
                .build();

        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertTrue(violations.isEmpty());
        assertEquals(AccountType.SAVINGS, account.getAccountType());
        assertEquals(BigDecimal.valueOf(1000), account.getBalance());
    }

    @Test
    @DisplayName("Should validate that account number is not null")
    void shouldValidateAccountNumberNotNull() {
        Account account = Account.builder()
                .accountNumber(null)
                .customerId(UUID.randomUUID())
                .customerName("Juan Perez")
                .accountType(AccountType.SAVINGS)
                .balance(BigDecimal.ZERO)
                .state(true)
                .build();

        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Account number is required")));
    }

    @Test
    @DisplayName("Should validate that account number is at least 100000")
    void shouldValidateAccountNumberMinimum() {
        Account account = Account.builder()
                .accountNumber(99999L)
                .customerId(UUID.randomUUID())
                .customerName("Juan Perez")
                .accountType(AccountType.SAVINGS)
                .balance(BigDecimal.ZERO)
                .state(true)
                .build();

        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("at least 100000")));
    }

    @Test
    @DisplayName("Should validate that account number cannot exceed 9999999999")
    void shouldValidateAccountNumberMaximum() {
        Account account = Account.builder()
                .accountNumber(10000000000L)
                .customerId(UUID.randomUUID())
                .customerName("Juan Perez")
                .accountType(AccountType.SAVINGS)
                .balance(BigDecimal.ZERO)
                .state(true)
                .build();

        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("cannot exceed 9999999999")));
    }

    @Test
    @DisplayName("Should validate that customer ID is not null")
    void shouldValidateCustomerIdNotNull() {
        Account account = Account.builder()
                .accountNumber(123456L)
                .customerId(null)
                .customerName("Juan Perez")
                .accountType(AccountType.SAVINGS)
                .balance(BigDecimal.ZERO)
                .state(true)
                .build();

        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Customer ID is required")));
    }

    @Test
    @DisplayName("Should validate that customer name is not blank")
    void shouldValidateCustomerNameNotBlank() {
        Account account = Account.builder()
                .accountNumber(123456L)
                .customerId(UUID.randomUUID())
                .customerName("  ")
                .accountType(AccountType.SAVINGS)
                .balance(BigDecimal.ZERO)
                .state(true)
                .build();

        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Customer name is required")));
    }

    @Test
    @DisplayName("Should validate that account type is not null")
    void shouldValidateAccountTypeNotNull() {
        Account account = Account.builder()
                .accountNumber(123456L)
                .customerId(UUID.randomUUID())
                .customerName("Juan Perez")
                .accountType(null)
                .balance(BigDecimal.ZERO)
                .state(true)
                .build();

        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Account type is required")));
    }

    @Test
    @DisplayName("Should validate that balance is not null")
    void shouldValidateBalanceNotNull() {
        Account account = Account.builder()
                .accountNumber(123456L)
                .customerId(UUID.randomUUID())
                .customerName("Juan Perez")
                .accountType(AccountType.SAVINGS)
                .balance(null)
                .state(true)
                .build();

        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Balance is required")));
    }

    @Test
    @DisplayName("Should validate that balance cannot be negative")
    void shouldValidateBalanceNotNegative() {
        Account account = Account.builder()
                .accountNumber(123456L)
                .customerId(UUID.randomUUID())
                .customerName("Juan Perez")
                .accountType(AccountType.SAVINGS)
                .balance(BigDecimal.valueOf(-100))
                .state(true)
                .build();

        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("cannot be negative")));
    }

    @Test
    @DisplayName("Should validate that state is not null")
    void shouldValidateStateNotNull() {
        Account account = Account.builder()
                .accountNumber(123456L)
                .customerId(UUID.randomUUID())
                .customerName("Juan Perez")
                .accountType(AccountType.SAVINGS)
                .balance(BigDecimal.ZERO)
                .state(null)
                .build();

        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("State is required")));
    }

    @Test
    @DisplayName("Should accept balance at zero boundary")
    void shouldAcceptBalanceAtZero() {
        Account account = Account.builder()
                .accountNumber(123456L)
                .customerId(UUID.randomUUID())
                .customerName("Juan Perez")
                .accountType(AccountType.SAVINGS)
                .balance(BigDecimal.ZERO)
                .state(true)
                .build();

        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should credit amount correctly")
    void shouldCreditAmountCorrectly() {
        Account account = Account.builder()
                .accountNumber(123456L)
                .customerId(UUID.randomUUID())
                .customerName("Juan Perez")
                .accountType(AccountType.SAVINGS)
                .balance(BigDecimal.valueOf(1000))
                .state(true)
                .build();

        Movement movement = account.credit(BigDecimal.valueOf(500));

        assertEquals(BigDecimal.valueOf(1500), account.getBalance());
        assertEquals(MovementType.CREDIT, movement.getMovementType());
        assertEquals(BigDecimal.valueOf(500), movement.getAmount());
        assertEquals(BigDecimal.valueOf(1000), movement.getBalanceBefore());
        assertEquals(BigDecimal.valueOf(1500), movement.getBalanceAfter());
    }

    @Test
    @DisplayName("Should debit amount correctly")
    void shouldDebitAmountCorrectly() {
        Account account = Account.builder()
                .accountNumber(123456L)
                .customerId(UUID.randomUUID())
                .customerName("Juan Perez")
                .accountType(AccountType.SAVINGS)
                .balance(BigDecimal.valueOf(1000))
                .state(true)
                .build();

        Movement movement = account.debit(BigDecimal.valueOf(300));

        assertEquals(BigDecimal.valueOf(700), account.getBalance());
        assertEquals(MovementType.DEBIT, movement.getMovementType());
        assertEquals(BigDecimal.valueOf(300), movement.getAmount());
        assertEquals(BigDecimal.valueOf(1000), movement.getBalanceBefore());
        assertEquals(BigDecimal.valueOf(700), movement.getBalanceAfter());
    }

    @Test
    @DisplayName("Should throw exception when debit exceeds balance")
    void shouldThrowExceptionWhenDebitExceedsBalance() {
        Account account = Account.builder()
                .accountNumber(123456L)
                .customerId(UUID.randomUUID())
                .customerName("Juan Perez")
                .accountType(AccountType.SAVINGS)
                .balance(BigDecimal.valueOf(100))
                .state(true)
                .build();

        assertThrows(InvalidBalanceException.class, () -> {
            account.debit(BigDecimal.valueOf(200));
        });
    }

    @Test
    @DisplayName("Should throw exception when credit amount is null")
    void shouldThrowExceptionWhenCreditAmountIsNull() {
        Account account = Account.builder()
                .accountNumber(123456L)
                .customerId(UUID.randomUUID())
                .customerName("Juan Perez")
                .accountType(AccountType.SAVINGS)
                .balance(BigDecimal.valueOf(1000))
                .state(true)
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            account.credit(null);
        });
    }

    @Test
    @DisplayName("Should throw exception when credit amount is zero or negative")
    void shouldThrowExceptionWhenCreditAmountIsZeroOrNegative() {
        Account account = Account.builder()
                .accountNumber(123456L)
                .customerId(UUID.randomUUID())
                .customerName("Juan Perez")
                .accountType(AccountType.SAVINGS)
                .balance(BigDecimal.valueOf(1000))
                .state(true)
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            account.credit(BigDecimal.ZERO);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            account.credit(BigDecimal.valueOf(-100));
        });
    }

    @Test
    @DisplayName("Should create account with CURRENT type")
    void shouldCreateAccountWithCurrentType() {
        Account account = Account.builder()
                .accountNumber(123456L)
                .customerId(UUID.randomUUID())
                .customerName("Maria Lopez")
                .accountType(AccountType.CURRENT)
                .balance(BigDecimal.valueOf(5000))
                .state(true)
                .build();

        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertTrue(violations.isEmpty());
        assertEquals(AccountType.CURRENT, account.getAccountType());
    }

    @Test
    @DisplayName("Should create inactive account")
    void shouldCreateInactiveAccount() {
        Account account = Account.builder()
                .accountNumber(123456L)
                .customerId(UUID.randomUUID())
                .customerName("Juan Perez")
                .accountType(AccountType.SAVINGS)
                .balance(BigDecimal.ZERO)
                .state(false)
                .build();

        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertTrue(violations.isEmpty());
        assertFalse(account.getState());
    }

    @Test
    @DisplayName("Should accept account number at minimum boundary")
    void shouldAcceptAccountNumberAtMinimumBoundary() {
        Account account = Account.builder()
                .accountNumber(100000L)
                .customerId(UUID.randomUUID())
                .customerName("Juan Perez")
                .accountType(AccountType.SAVINGS)
                .balance(BigDecimal.ZERO)
                .state(true)
                .build();

        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should accept account number at maximum boundary")
    void shouldAcceptAccountNumberAtMaximumBoundary() {
        Account account = Account.builder()
                .accountNumber(9999999999L)
                .customerId(UUID.randomUUID())
                .customerName("Juan Perez")
                .accountType(AccountType.SAVINGS)
                .balance(BigDecimal.ZERO)
                .state(true)
                .build();

        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertTrue(violations.isEmpty());
    }
}
