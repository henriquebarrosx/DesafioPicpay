package com.picpay.bankapi.web.dto;

import com.picpay.bankapi.entity.AccountTypeEnum;

import java.math.BigDecimal;

public record AccountDTO(
        String name,
        BigDecimal balance,
        AccountTypeEnum type,
        String cpfCnpj,
        String email
) { }
