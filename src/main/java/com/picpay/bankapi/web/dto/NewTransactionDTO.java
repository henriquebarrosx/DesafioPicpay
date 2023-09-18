package com.picpay.bankapi.web.dto;

import java.math.BigDecimal;

public record NewTransactionDTO(
    BigDecimal value,
    Long payerId,
    Long payeeId
) {}
