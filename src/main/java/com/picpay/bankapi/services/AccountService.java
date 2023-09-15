package com.picpay.bankapi.services;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import com.picpay.bankapi.entities.Account;
import com.picpay.bankapi.controllers.DTOs.NewUserDTO;
import com.picpay.bankapi.exceptions.NotFoundException;
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
                .type(params.getType())
                .balance(params.getBalance())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        var foundAccount = accountRepository
                .existsByCpfCnpjOrEmail(params.getCpfCnpj(), params.getEmail());

        if (foundAccount.isPresent()) {
            throw new AccountAlreadyRegisteredException("CPF/CNPJ ou e-mail já existente");
        }

        return accountRepository.save(account);
    }

    public Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário com id " + id + " não encontrado"));
    }
}
