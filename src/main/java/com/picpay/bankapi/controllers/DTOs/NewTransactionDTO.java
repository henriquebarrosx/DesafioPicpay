package com.picpay.bankapi.controllers.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewTransactionDTO {
    private double value;
    private Long payerId;
    private Long payeeId;
}
