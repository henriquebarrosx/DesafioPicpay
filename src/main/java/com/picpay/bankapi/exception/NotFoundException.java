package com.picpay.bankapi.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(Long id) {
        super(String.format("Resource %s not found", id));
    }
}
