package com.picpay.bankapi.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import com.picpay.bankapi.entities.Account;
import com.picpay.bankapi.controllers.DTOs.NewUserDTO;
import com.picpay.bankapi.repositories.AccountRepository;
import com.picpay.bankapi.exceptions.AccountAlreadyRegisteredException;

@Service
@AllArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    public Account create(NewUserDTO params) {
        var account = Account
                .builder()
                .name(params.getName())
                .email(params.getEmail())
                .cpfCnpj(params.getCpfCnpj())
                .password(params.getPassword())
                .build();

        var foundAccount = accountRepository.existsByCpfCnpjOrEmail(params.getCpfCnpj(), params.getEmail());

        if (foundAccount.isPresent()) {
            throw new AccountAlreadyRegisteredException("CPF/CPNJ ou e-mail já existente");
        }

        return accountRepository.save(account);
    }
}
