package com.picpay.bankapi.builders;

import com.picpay.bankapi.entity.Account;
import com.picpay.bankapi.entity.AccountTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountBuilder {

    public static Account buildNewAccountParams() {
        return Account
                .builder()
                .type(AccountTypeEnum.COMMON)
                .name("John Doe")
                .balance(BigDecimal.valueOf(100.0))
                .cpfCnpj("86307077042")
                .email("john.doe@mail.com")
                .password("12345678")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Account buildCreatedAccount() {
        return Account
                .builder()
                .id(1L)
                .type(AccountTypeEnum.COMMON)
                .name("John Doe")
                .balance(BigDecimal.valueOf(100.0))
                .cpfCnpj("86307077042")
                .email("john.doe@mail.com")
                .password("12345678")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

}
