package com.btoapanta.account.service.infrastructure.adapter.ouput.rest;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String message) {
        super(message);
    }
}
