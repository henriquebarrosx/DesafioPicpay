package com.picpay.bankapi.account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import com.picpay.bankapi.exception.NotFoundException;
import com.picpay.bankapi.exception.IllegalOperationException;


@Service
@AllArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    public Account create(NewAccountDTO params) {
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
                .findByCpfCnpjOrEmail(params.getCpfCnpj(), params.getEmail());

        if (foundAccount.isPresent()) {
            throw new IllegalOperationException("CPF/CNPJ or e-mail already registered ");
        }

        return accountRepository.save(account);
    }

    public void increaseBalance(Account account, BigDecimal amount) {
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
    }

    public void subtractBalance(Account account, BigDecimal amount) {
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
    }

    public Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User %s not found", id)));
    }
}