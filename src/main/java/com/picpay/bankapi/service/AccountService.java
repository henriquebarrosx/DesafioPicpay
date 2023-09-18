package com.picpay.bankapi.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.picpay.bankapi.entity.Account;
import com.picpay.bankapi.repository.AccountRepository;
import com.picpay.bankapi.web.dto.AccountDTO;
import com.picpay.bankapi.web.dto.NewAccountDTO;
import com.picpay.bankapi.web.mapper.AccountDTOMapper;
import com.picpay.bankapi.web.mapper.NewAccountDTOMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.picpay.bankapi.exception.NotFoundException;
import com.picpay.bankapi.exception.IllegalOperationException;

@Service
@Slf4j
@AllArgsConstructor
public class AccountService {
    private final AccountDTOMapper accountDTOMapper;
    private final AccountRepository accountRepository;
    private final NewAccountDTOMapper newAccountDTOMapper;

    public Account createAccount(NewAccountDTO newAccountDTO) {
        Account account = newAccountDTOMapper.apply(newAccountDTO);
        log.info("Creating new account: {}", account);

        var accountWithCpfCnpjOrEmail = accountRepository
                .findByCpfCnpjOrEmail(newAccountDTO.getCpfCnpj(), newAccountDTO.getEmail());

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

    public List<AccountDTO> findAll() {
        log.info("Finding all accounts");
        return accountRepository
                .findAll().stream()
                .map(accountDTOMapper)
                .collect(Collectors.toList());
    }
}
