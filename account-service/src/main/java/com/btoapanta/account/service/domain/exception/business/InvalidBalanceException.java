package com.btoapanta.account.service.domain.exception.business;

import com.btoapanta.account.service.domain.exception.DomainException;

import java.math.BigDecimal;

public class InvalidBalanceException extends DomainException {
    public InvalidBalanceException(BigDecimal attemptedBalance) {
        super(String.format("Invalid balance: %s. The balance couldn't be negative", attemptedBalance));
    }

    public InvalidBalanceException(String message) {
        super(message);
    }
}
