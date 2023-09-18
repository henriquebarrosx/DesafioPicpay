package com.picpay.bankapi.account;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import com.picpay.bankapi.exception.NotFoundException;
import com.picpay.bankapi.exception.IllegalOperationException;


@Service
@AllArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountDTOMapper accountDTOMapper;

    public Account createAccount(AccountDTO accountDTO) {
        Account account = accountDTOMapper.apply(accountDTO);

        var accountWithCpfCnpjOrEmail = accountRepository
                .findByCpfCnpjOrEmail(accountDTO.cpfCnpj(), accountDTO.email());

        if (accountWithCpfCnpjOrEmail.isPresent()) {
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
