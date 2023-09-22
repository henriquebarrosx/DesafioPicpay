package com.picpay.bankapi.service;

import com.picpay.bankapi.builders.EmailBuilder;
import com.picpay.bankapi.exception.IllegalOperationException;
import com.picpay.bankapi.repository.TransactionRepository;
import com.picpay.bankapi.builders.TransactionBuilder;
import com.picpay.bankapi.builders.AccountBuilder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.mail.MailSendException;
import org.mockito.junit.jupiter.MockitoExtension;
import com.picpay.bankapi.entity.Transaction;
import com.picpay.bankapi.web.dto.EmailDTO;
import com.picpay.bankapi.entity.Account;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @InjectMocks
    TransactionService transactionService;

    @Mock
    EmailService emailService;

    @Mock
    AccountService accountService;

    @Mock
    TransactionRepository transactionRepository;

    @Test
    void shouldReturnInvalidAccountTypeWhenCreatingTransaction() {
        BigDecimal amount = BigDecimal.valueOf(10);
        BigDecimal payerBalance = BigDecimal.valueOf(100);
        BigDecimal payeeBalance = BigDecimal.valueOf(100);
        Account payer = AccountBuilder.buildShopkeeperAccountType(payerBalance);
        Account payee = AccountBuilder.buildCommonAccountType(payeeBalance);

        Mockito.when(accountService.findById(ArgumentMatchers.any()))
                .thenReturn(payer)
                .thenReturn(payee);

        var exception = Assertions.assertThrows(
                IllegalOperationException.class,
                () -> transactionService.createTransaction(amount, payer.getId(), payee.getId())
        );

        Assertions.assertEquals("Only common user type can send transactions.", exception.getMessage());
        Mockito.verify(transactionRepository, Mockito.never()).save(ArgumentMatchers.any());
    }

    @Test
    void shouldReturnInvalidPayeeWhenCreatingTransaction() {
        BigDecimal amount = BigDecimal.valueOf(10);
        BigDecimal payerBalance = BigDecimal.valueOf(100);
        Account payer = AccountBuilder.buildCommonAccountType(payerBalance);
        ArgumentCaptor<Long> accountsIdCaptor = ArgumentCaptor.forClass(Long.class);

        Mockito.when(accountService.findById(ArgumentMatchers.any()))
                .thenReturn(payer);

        var exception = Assertions.assertThrows(
                IllegalOperationException.class,
                () -> transactionService.createTransaction(amount, payer.getId(), payer.getId())
        );

        Assertions.assertEquals("Payer and payee account should be different.", exception.getMessage());
        Mockito.verify(transactionRepository, Mockito.never()).save(ArgumentMatchers.any());
        Mockito.verify(accountService, Mockito.times(2)).findById(accountsIdCaptor.capture());
        Assertions.assertEquals(List.of(payer.getId(), payer.getId()), accountsIdCaptor.getAllValues());
    }

    @Test
    void shouldReturnInsufficientBalanceWhenCreatingTransaction() {
        BigDecimal amount = BigDecimal.valueOf(1000);
        BigDecimal payerBalance = BigDecimal.valueOf(100);
        BigDecimal payeeBalance = BigDecimal.valueOf(100);
        Account payer = AccountBuilder.buildCommonAccountType(payerBalance);
        Account payee = AccountBuilder.buildShopkeeperAccountType(payeeBalance);

        Mockito.when(accountService.findById(ArgumentMatchers.any()))
                .thenReturn(payer)
                .thenReturn(payee);

        var exception = Assertions.assertThrows(
                IllegalOperationException.class,
                () -> transactionService.createTransaction(amount, payer.getId(), payer.getId())
        );

        Assertions.assertEquals("insufficient balance.", exception.getMessage());
        Mockito.verify(transactionRepository, Mockito.never()).save(ArgumentMatchers.any());
    }

    @Test
    void shouldDecreasePayerBalanceWhenCreatingTransaction() {
        BigDecimal amount = BigDecimal.valueOf(10);
        BigDecimal payerBalance = BigDecimal.valueOf(100);
        BigDecimal payeeBalance = BigDecimal.valueOf(100);
        BigDecimal expectedBalance = payerBalance.subtract(amount);
        Account payer = AccountBuilder.buildCommonAccountType(payerBalance);
        Account payee = AccountBuilder.buildShopkeeperAccountType(payeeBalance);
        Transaction expected = TransactionBuilder.buildStoredTransaction(payer, payee, amount);

        Mockito.when(accountService.findById(ArgumentMatchers.any()))
                .thenReturn(payer)
                .thenReturn(payee);

        Mockito.when(transactionRepository.save(ArgumentMatchers.any()))
                .thenReturn(expected);

        Transaction result = transactionService
                .createTransaction(amount, payer.getId(), payee.getId());

        Assertions.assertEquals(expectedBalance, result.getPayer().getBalance());
    }

    @Test
    void shouldIncreasePayeeBalanceWhenCreatingTransaction() {
        BigDecimal amount = BigDecimal.valueOf(10);
        BigDecimal payerBalance = BigDecimal.valueOf(100);
        BigDecimal payeeBalance = BigDecimal.valueOf(100);
        BigDecimal expectedBalance = payeeBalance.add(amount);
        Account payer = AccountBuilder.buildCommonAccountType(payerBalance);
        Account payee = AccountBuilder.buildShopkeeperAccountType(payeeBalance);
        Transaction transaction = TransactionBuilder.buildStoredTransaction(payer, payee, amount);

        Mockito.when(accountService.findById(ArgumentMatchers.any()))
                .thenReturn(payer)
                .thenReturn(payee);

        Mockito.when(transactionRepository.save(ArgumentMatchers.any()))
                .thenReturn(transaction);

        Transaction result = transactionService
                .createTransaction(amount, payer.getId(), payee.getId());

        Assertions.assertEquals(expectedBalance, result.getPayee().getBalance());
    }

    @Test
    void shouldReturnStoredTransactionWhenIsValid() {
        BigDecimal amount = BigDecimal.valueOf(10);
        BigDecimal payerBalance = BigDecimal.valueOf(100);
        BigDecimal payeeBalance = BigDecimal.valueOf(100);
        Account payer = AccountBuilder.buildCommonAccountType(payerBalance);
        Account payee = AccountBuilder.buildShopkeeperAccountType(payeeBalance);
        Transaction expected = TransactionBuilder.buildStoredTransaction(payer, payee, amount);

        Mockito.when(accountService.findById(ArgumentMatchers.any()))
                .thenReturn(payer)
                .thenReturn(payee);

        Mockito.when(transactionRepository.save(ArgumentMatchers.any()))
                .thenReturn(expected);

        Transaction result = transactionService
                .createTransaction(amount, payer.getId(), payee.getId());

        Assertions.assertEquals(expected, result);
    }

    @Test
    void shouldSendEmailToReceiverWhenTransactionIsCreated() {
        BigDecimal amount = BigDecimal.valueOf(10);
        BigDecimal payerBalance = BigDecimal.valueOf(100);
        BigDecimal payeeBalance = BigDecimal.valueOf(100);
        Account payer = AccountBuilder.buildCommonAccountType(payerBalance);
        Account payee = AccountBuilder.buildShopkeeperAccountType(payeeBalance);
        Transaction transaction = TransactionBuilder.buildStoredTransaction(payer, payee, amount);

        EmailDTO expected = EmailBuilder.buildEmailPayload(payee, amount);

        Mockito.when(accountService.findById(ArgumentMatchers.any()))
                .thenReturn(payer)
                .thenReturn(payee);

        Mockito.when(transactionRepository.save(ArgumentMatchers.any()))
                .thenReturn(transaction);

        transactionService
                .createTransaction(amount, payer.getId(), payee.getId());

        Mockito.verify(emailService, Mockito.times(1)).sendEmail(expected);
    }

    @Test
    void shouldReturnMailSendExceptionWhenSendingEmailToPayeeFailed() {
        BigDecimal amount = BigDecimal.valueOf(10);
        BigDecimal payerBalance = BigDecimal.valueOf(100);
        BigDecimal payeeBalance = BigDecimal.valueOf(100);
        Account payer = AccountBuilder.buildCommonAccountType(payerBalance);
        Account payee = AccountBuilder.buildShopkeeperAccountType(payeeBalance);

        Mockito.when(accountService.findById(ArgumentMatchers.any()))
                .thenReturn(payer)
                .thenReturn(payee);

        Mockito.doThrow(MailSendException.class)
                .when(emailService)
                .sendEmail(ArgumentMatchers.any());

        Assertions.assertThrows(
                MailSendException.class,
                () -> transactionService
                        .createTransaction(amount, payer.getId(), payee.getId())
        );
    }

    @Test
    void createChargeback() {
    }

    @Test
    void findById() {
    }

    @Test
    void findAll() {
    }
}