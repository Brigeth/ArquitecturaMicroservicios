package com.btoapanta.account.service.domain.exception.notfound;

import com.btoapanta.account.service.domain.exception.DomainException;

public class AccountNotFoundException extends DomainException {
    public AccountNotFoundException(Long accountNumber) {
        super("Account not found with number: " + accountNumber);
    }
}
