package com.btoapanta.account.service.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;

import com.btoapanta.account.service.domain.enums.AccountType;
import com.btoapanta.account.service.domain.enums.MovementType;
import com.btoapanta.account.service.domain.exception.business.InvalidBalanceException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private UUID id;
    
    @NotNull(message = "Account number is required")
    @Min(value = 100000L, message = "Account number must be at least 100000")
    @Max(value = 9999999999L, message = "Account number cannot exceed 9999999999")
    private Long accountNumber;
    
    @NotNull(message = "Customer ID is required")
    private UUID customerId;
    
    @NotBlank(message = "Customer name is required")
    private String customerName;
    
    @NotNull(message = "Account type is required")
    private AccountType accountType;
    
    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Balance cannot be negative")
    private BigDecimal balance;
    
    @NotNull(message = "State is required")
    private Boolean state;
    
    @Valid
    @Builder.Default
    private List<Movement> movements = new ArrayList<>();

    public Movement debit(BigDecimal amount) {
        validateAmount(amount);
        BigDecimal balanceBefore = this.balance;
        this.balance = this.balance.subtract(amount);
        validateBalance();
        Movement movement = createMovement(MovementType.DEBIT, amount, balanceBefore);
        this.movements.add(movement);
        return movement;
    }
    public Movement credit(BigDecimal amount) {
        validateAmount(amount);

        BigDecimal balanceBefore = this.balance;
        this.balance = this.balance.add(amount);

        Movement movement = createMovement(MovementType.CREDIT, amount, balanceBefore);
        this.movements.add(movement);

        return movement;
    }

    private Movement createMovement(MovementType type, BigDecimal amount, BigDecimal balanceBefore) {
        return Movement.builder()
                .accountNumber(this.accountNumber)
                .movementType(type)
                .amount(amount)
                .balanceBefore(balanceBefore)
                .balanceAfter(this.balance)
                .date(LocalDateTime.now())
                .build();
    }

    private void validateBalance() {
        if (this.balance != null && this.balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidBalanceException(this.balance);
        }
    }
    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("The amount must be greater than zero.");
        }
    }




}