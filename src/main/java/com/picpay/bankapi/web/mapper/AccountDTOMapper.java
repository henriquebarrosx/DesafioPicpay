package com.picpay.bankapi.web.mapper;

import com.picpay.bankapi.web.dto.AccountDTO;
import com.picpay.bankapi.entity.Account;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class AccountDTOMapper implements Function<Account, AccountDTO> {
    @Override
    public AccountDTO apply(Account account) {
        return new AccountDTO(
                account.getName(),
                account.getBalance(),
                account.getType(),
                account.getCpfCnpj(),
                account.getEmail()
        );
    }
}
