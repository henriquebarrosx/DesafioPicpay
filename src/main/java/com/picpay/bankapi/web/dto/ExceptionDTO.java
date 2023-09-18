package com.picpay.bankapi.web.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class ExceptionDTO {
    private int status;
    private String message;
}
