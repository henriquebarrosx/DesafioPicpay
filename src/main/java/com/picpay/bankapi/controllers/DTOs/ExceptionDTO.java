package com.picpay.bankapi.controllers.DTOs;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class ExceptionDTO {
    private int status;
    private String message;
}
