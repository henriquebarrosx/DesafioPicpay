package com.picpay.bankapi.service;

import com.picpay.bankapi.exception.IllegalOperationException;
import com.picpay.bankapi.repository.AccountRepository;
import com.picpay.bankapi.exception.NotFoundException;
import com.picpay.bankapi.builders.AccountBuilder;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import com.picpay.bankapi.entity.Account;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @InjectMocks
    AccountService accountService;

    @Mock
    AccountRepository accountRepository;

    @Test
    void shouldCreateNewAccount() {
        Account account = AccountBuilder.buildNewAccountParams();
        Account expected = AccountBuilder.buildStoredAccount();

        Mockito.when(accountRepository.findByCpfCnpjOrEmail(account.getCpfCnpj(), account.getEmail()))
                .thenReturn(Optional.empty());

        Mockito.when(accountRepository.save(account))
                .thenReturn(expected);

        Account result = accountService.createAccount(account);

        Mockito.verify(accountRepository, Mockito.times(1)).findByCpfCnpjOrEmail(expected.getCpfCnpj(), expected.getEmail());
        Mockito.verify(accountRepository, Mockito.times(1)).save(account);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void shouldReturnUserAlreadyExistExceptionWhenCreatingNewAccount() {
        Account account = AccountBuilder.buildNewAccountParams();
        Account expected = AccountBuilder.buildStoredAccount();

        Mockito.when(accountRepository.findByCpfCnpjOrEmail(account.getCpfCnpj(), account.getEmail()))
                .thenReturn(Optional.of(expected));

        Assertions.assertThrows(IllegalOperationException.class, () -> accountService.createAccount(account));
        Mockito.verify(accountRepository, Mockito.times(1)).findByCpfCnpjOrEmail(expected.getCpfCnpj(), expected.getEmail());
        Mockito.verify(accountRepository, Mockito.never()).save(account);
    }

    @Test
    void shouldIncreaseAccountBalance() {
        Account expected = AccountBuilder.buildStoredAccount();
        BigDecimal amount = BigDecimal.valueOf(100.0);

        accountService.increaseBalance(expected, amount);

        Assertions.assertEquals(expected.getBalance(), BigDecimal.valueOf(200.0));
        Mockito.verify(accountRepository, Mockito.times(1)).save(expected);
    }

    @Test
    void shouldSubtractAccountBalance() {
        Account expected = AccountBuilder.buildStoredAccount();
        BigDecimal amount = BigDecimal.valueOf(25.0);

        accountService.subtractBalance(expected, amount);

        Assertions.assertEquals(expected.getBalance(), BigDecimal.valueOf(75.0));
        Mockito.verify(accountRepository, Mockito.times(1)).save(expected);
    }

    @Test
    void shouldFindAccountById() {
        Account expected = AccountBuilder.buildStoredAccount();

        Mockito.when(accountRepository.findById(expected.getId()))
                        .thenReturn(Optional.of(expected));

        Account actual = accountService.findById(expected.getId());
        Mockito.verify(accountRepository, Mockito.times(1)).findById(expected.getId());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void shouldNotFindAccountById() {
        Account expected = AccountBuilder.buildStoredAccount();

        Mockito.when(accountRepository.findById(expected.getId()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> accountService.findById(expected.getId()));
        Mockito.verify(accountRepository, Mockito.times(1)).findById(expected.getId());
    }

    @Test
    void shouldReturnEmptyAccountList() {
        List<Account> expected = new ArrayList<>();

        Mockito.when(accountRepository.findAll()).thenReturn(expected);
        List<Account> accounts = accountService.findAll();

        Mockito.verify(accountRepository, Mockito.times(1)).findAll();
        Assertions.assertEquals(expected, accounts);
    }

    @Test
    void shouldReturnFilledAccountList() {
        List<Account> expected = List.of(AccountBuilder.buildStoredAccount());

        Mockito.when(accountRepository.findAll()).thenReturn(expected);
        List<Account> accounts = accountService.findAll();

        Mockito.verify(accountRepository, Mockito.times(1)).findAll();
        Assertions.assertEquals(expected, accounts);
    }
}