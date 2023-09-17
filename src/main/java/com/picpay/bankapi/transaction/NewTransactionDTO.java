package com.picpay.bankapi.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class NewTransactionDTO {
    private BigDecimal value;
    private Long payerId;
    private Long payeeId;
}
