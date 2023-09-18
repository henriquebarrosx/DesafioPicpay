package com.picpay.bankapi.account;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.function.Function;

@Service
public class AccountDTOMapper implements Function<AccountDTO, Account> {
    @Override
    public Account apply(AccountDTO accountDTO) {
        return Account
                .builder()
                .name(accountDTO.name())
                .email(accountDTO.email())
                .cpfCnpj(accountDTO.cpfCnpj())
                .password(accountDTO.password())
                .type(accountDTO.type())
                .balance(accountDTO.balance())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
