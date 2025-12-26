package com.btoapanta.account.service.domain.exception;

public class DuplicateAccountException extends RuntimeException {
    public DuplicateAccountException(Long accountNumber) {
        super("Account already exists with number: " + accountNumber);
    }
}
