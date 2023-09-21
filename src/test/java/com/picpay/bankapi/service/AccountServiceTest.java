package com.picpay.bankapi.service;

import com.picpay.bankapi.exception.IllegalOperationException;
import com.picpay.bankapi.repository.AccountRepository;
import com.picpay.bankapi.builders.AccountBuilder;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import com.picpay.bankapi.entity.Account;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.*;

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
        Account expected = AccountBuilder.buildCreatedAccount();

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
        Account expected = AccountBuilder.buildCreatedAccount();

        Mockito.when(accountRepository.findByCpfCnpjOrEmail(account.getCpfCnpj(), account.getEmail()))
                .thenReturn(Optional.of(expected));

        Assertions.assertThrows(IllegalOperationException.class, () -> accountService.createAccount(account));
        Mockito.verify(accountRepository, Mockito.times(1)).findByCpfCnpjOrEmail(expected.getCpfCnpj(), expected.getEmail());
        Mockito.verify(accountRepository, Mockito.never()).save(account);
    }

    @Test
    void increaseBalance() {
    }

    @Test
    void subtractBalance() {
    }

    @Test
    void findById() {
    }

    @Test
    void findAll() {
    }
}