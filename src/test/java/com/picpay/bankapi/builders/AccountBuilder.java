package com.picpay.bankapi.builders;

import com.picpay.bankapi.entity.Account;
import com.picpay.bankapi.entity.AccountTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;

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
                .createdAt(LocalDateTime.of(2023, Month.SEPTEMBER, 1, 10, 0, 0))
                .updatedAt(LocalDateTime.of(2023, Month.SEPTEMBER, 1, 10, 0, 0))
                .build();
    }

    public static Account buildStoredAccount() {
        return Account
                .builder()
                .id(1L)
                .type(AccountTypeEnum.COMMON)
                .name("John Doe")
                .balance(BigDecimal.valueOf(100.0))
                .cpfCnpj("86307077042")
                .email("john.doe@mail.com")
                .password("12345678")
                .createdAt(LocalDateTime.of(2023, Month.SEPTEMBER, 1, 10, 0, 0))
                .updatedAt(LocalDateTime.of(2023, Month.SEPTEMBER, 1, 10, 0, 0))
                .build();
    }

    public static Account buildCommonAccountType(BigDecimal balance) {
        return Account
                .builder()
                .id(1L)
                .type(AccountTypeEnum.COMMON)
                .name("John Doe")
                .balance(balance)
                .cpfCnpj("86307077042")
                .email("john.doe@mail.com")
                .password("12345678")
                .createdAt(LocalDateTime.of(2023, Month.SEPTEMBER, 1, 10, 0, 0))
                .updatedAt(LocalDateTime.of(2023, Month.SEPTEMBER, 1, 10, 0, 0))
                .build();
    }

    public static Account buildShopkeeperAccountType(BigDecimal balance) {
        return Account
                .builder()
                .id(2L)
                .type(AccountTypeEnum.SHOPKEEPER)
                .name("Ana Maria")
                .balance(balance)
                .cpfCnpj("98186340041")
                .email("ana.maria@mail.com")
                .password("12345678")
                .createdAt(LocalDateTime.of(2023, Month.SEPTEMBER, 1, 10, 0, 0))
                .updatedAt(LocalDateTime.of(2023, Month.SEPTEMBER, 1, 10, 0, 0))
                .build();
    }
}
