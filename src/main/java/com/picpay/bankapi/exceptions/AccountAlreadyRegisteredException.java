package com.picpay.bankapi.exceptions;

public class AccountAlreadyRegisteredException extends RuntimeException {
    public AccountAlreadyRegisteredException(String message) {
        super(message);
    }
}
