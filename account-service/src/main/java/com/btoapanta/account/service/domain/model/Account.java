package com.btoapanta.account.service.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;

import com.btoapanta.account.service.domain.enums.AccountType;
import com.btoapanta.account.service.domain.enums.MovementType;
import com.btoapanta.account.service.domain.exception.business.InvalidBalanceException;
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
    private Long accountNumber;
    private UUID customerId;
    private String customerName;
    private AccountType accountType;
    private BigDecimal balance;
    private Boolean state;
    @Builder.Default
    private List<Movement> movements = new ArrayList<>();

    // ========== COMPORTAMIENTO DE NEGOCIO ==========

    public Movement debit(BigDecimal amount) {
        validateAmount(amount);
        BigDecimal balanceBefore = this.balance;
        this.balance = this.balance.subtract(amount);
        validateBalance();
        Movement movement = createMovement(MovementType.DEBITO, amount, balanceBefore);
        this.movements.add(movement);
        return movement;
    }
    public Movement credit(BigDecimal amount) {
        validateAmount(amount);

        BigDecimal balanceBefore = this.balance;
        this.balance = this.balance.add(amount);

        Movement movement = createMovement(MovementType.CREDITO, amount, balanceBefore);
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

    // ========== VALIDACIONES ==========
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