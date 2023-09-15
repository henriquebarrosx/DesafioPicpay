package com.picpay.bankapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AccountAlreadyRegisteredException extends RuntimeException {
    public AccountAlreadyRegisteredException(String message) {
        super(message);
    }
}
