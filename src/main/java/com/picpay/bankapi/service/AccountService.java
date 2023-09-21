package com.picpay.bankapi.service;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.picpay.bankapi.entity.Account;
import com.picpay.bankapi.exception.NotFoundException;
import com.picpay.bankapi.repository.AccountRepository;
import com.picpay.bankapi.exception.IllegalOperationException;

@Service
@Slf4j
@AllArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    public Account createAccount(Account account) {
        log.info("Creating new account: {}", account);

        var accountWithCpfCnpjOrEmail = accountRepository
                .findByCpfCnpjOrEmail(account.getCpfCnpj(), account.getEmail());

        if (accountWithCpfCnpjOrEmail.isPresent()) {
            throw new IllegalOperationException("CPF/CNPJ or e-mail already registered.");
        }

        return accountRepository.save(account);
    }

    public void increaseBalance(Account account, BigDecimal amount) {
        log.info("Increasing account balance: {} {}", account, amount);
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
    }

    public void subtractBalance(Account account, BigDecimal amount) {
        log.info("Subtracting account balance: {} {}", account, amount);
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
    }

    public Account findById(Long id) {
        log.info("Finding account by id: {}", id);
        return accountRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(id));
    }

    public List<Account> findAll() {
        log.info("Finding all accounts");
        return accountRepository.findAll();
    }
}
