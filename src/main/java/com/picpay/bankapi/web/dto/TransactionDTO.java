package com.picpay.bankapi.web.dto;

import java.math.BigDecimal;

public record TransactionDTO(
    Long id,
    AccountDTO payer,
    AccountDTO payee,
    BigDecimal value,
    Boolean isChargeback
) { }
