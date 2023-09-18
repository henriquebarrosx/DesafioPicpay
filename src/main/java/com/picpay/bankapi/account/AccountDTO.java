package com.picpay.bankapi.account;

import java.math.BigDecimal;

public record AccountDTO(
        AccountTypeEnum type,
        String name,
        BigDecimal balance,
        String cpfCnpj,
        String email,
        String password
) {}
