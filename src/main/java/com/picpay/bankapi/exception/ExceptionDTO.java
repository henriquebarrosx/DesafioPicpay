package com.picpay.bankapi.exception;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class ExceptionDTO {
    private int status;
    private String message;
}
