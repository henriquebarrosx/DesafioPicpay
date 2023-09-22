package com.picpay.bankapi.service;

import com.picpay.bankapi.exception.IllegalOperationException;
import com.picpay.bankapi.repository.TransactionRepository;
import com.picpay.bankapi.builders.TransactionBuilder;
import com.picpay.bankapi.exception.NotFoundException;
import com.picpay.bankapi.builders.AccountBuilder;
import com.picpay.bankapi.builders.EmailBuilder;
import com.picpay.bankapi.web.dto.EmailDTO;
import com.picpay.bankapi.entity.Account;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.mail.MailSendException;
import org.mockito.junit.jupiter.MockitoExtension;
import com.picpay.bankapi.entity.Transaction;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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

        Mockito.verify(transactionRepository, Mockito.never()).save(ArgumentMatchers.any());
        assertThatThrownBy(() -> transactionService.createTransaction(amount, payer.getId(), payee.getId()))
                .isExactlyInstanceOf(IllegalOperationException.class)
                .hasMessageContaining("Only common user type can send transactions.");
    }

    @Test
    void shouldReturnInvalidPayeeWhenCreatingTransaction() {
        BigDecimal amount = BigDecimal.valueOf(10);
        BigDecimal payerBalance = BigDecimal.valueOf(100);
        Account payer = AccountBuilder.buildCommonAccountType(payerBalance);
        ArgumentCaptor<Long> accountsIdCaptor = ArgumentCaptor.forClass(Long.class);

        Mockito.when(accountService.findById(ArgumentMatchers.any()))
                .thenReturn(payer);

        assertThatThrownBy(() -> transactionService.createTransaction(amount, payer.getId(), payer.getId()))
                .isExactlyInstanceOf(IllegalOperationException.class)
                .hasMessageContaining("Payer and payee account should be different.");
        Mockito.verify(accountService, Mockito.times(2)).findById(accountsIdCaptor.capture());
        assertThat(accountsIdCaptor.getAllValues()).isEqualTo(List.of(payer.getId(), payer.getId()));
        Mockito.verify(transactionRepository, Mockito.never()).save(ArgumentMatchers.any());
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

        Mockito.verify(transactionRepository, Mockito.never()).save(ArgumentMatchers.any());
        assertThatThrownBy(() -> transactionService.createTransaction(amount, payer.getId(), payer.getId()))
                .isExactlyInstanceOf(IllegalOperationException.class)
                .hasMessageContaining("insufficient balance.");
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

        assertThat(result.getPayer().getBalance()).isEqualTo(expectedBalance);
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

        assertThat(result.getPayee().getBalance()).isEqualTo(expectedBalance);
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

        assertThat(result).isEqualTo(expected);
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

        Mockito.verify(emailService, Mockito.times(1))
                .sendEmail(expected);
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

        assertThatThrownBy(() -> transactionService.createTransaction(amount, payer.getId(), payee.getId()))
                .isExactlyInstanceOf(MailSendException.class);
    }

    @Test
    void shouldReturnIllegalOperationExceptionWhenGeneratingChargebackFromReversedTransaction() {
        Transaction transaction = TransactionBuilder.buildTransactionWithId(1L);
        Transaction reversedTransaction = TransactionBuilder.buildReversedFrom(transaction);

        Mockito.when(transactionRepository.findById(ArgumentMatchers.any()))
                .thenReturn(Optional.of(reversedTransaction));

        assertThatThrownBy(() -> transactionService.createChargeback(transaction.getId()))
                .isExactlyInstanceOf(IllegalOperationException.class)
                .hasMessageContaining("Transaction only can be reverted once.");
    }

    @Test
    void shouldReturnIllegalOperationExceptionWhenTryGenerateChargebackFromChargebackTransactionType() {
        Transaction transaction = TransactionBuilder.buildTransactionWithId(1L);
        Transaction chargeback = TransactionBuilder.buildChargebackFrom(transaction);

        Mockito.when(transactionRepository.findById(ArgumentMatchers.any()))
                .thenReturn(Optional.of(chargeback));

        assertThatThrownBy(() -> transactionService.createChargeback(transaction.getId()))
                .isExactlyInstanceOf(IllegalOperationException.class)
                .hasMessageContaining("The transaction is not able to be reversed.");
    }

    @Test
    void shouldValidateIfTransactionUpdatedWithReversedWhenCreatingChargeback() {
        Transaction transaction = TransactionBuilder.buildTransactionWithId(1L);
        Transaction reversedTransaction = TransactionBuilder.buildReversedFrom(transaction);
        ArgumentCaptor<Transaction> transactionArgCaptor = ArgumentCaptor.forClass(Transaction.class);

        Mockito.when(transactionRepository.findById(ArgumentMatchers.any()))
                .thenReturn(Optional.of(transaction));

        Mockito.when(accountService.findById(ArgumentMatchers.any()))
                .thenReturn(transaction.getPayer())
                .thenReturn(transaction.getPayee());

        Mockito.when(transactionRepository.save(ArgumentMatchers.any()))
                .thenReturn(ArgumentMatchers.any());

        transactionService.createChargeback(transaction.getId());

        Mockito.verify(transactionRepository, Mockito.times(2))
                .save(transactionArgCaptor.capture());
        assertThat(transactionArgCaptor.getAllValues())
                .asList()
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("createdAt", "updatedAt")
                .contains(reversedTransaction);
    }

    @Test
    void shouldSaveChargebackWithReversedWhenCreatingChargeback() {
        Transaction transaction = TransactionBuilder.buildTransactionWithId(1L);
        Transaction chargeback = TransactionBuilder.buildChargebackFrom(transaction);
        ArgumentCaptor<Transaction> transactionArgCaptor = ArgumentCaptor.forClass(Transaction.class);

        Mockito.when(transactionRepository.findById(ArgumentMatchers.any()))
                .thenReturn(Optional.of(transaction));

        Mockito.when(accountService.findById(ArgumentMatchers.any()))
                .thenReturn(transaction.getPayer())
                .thenReturn(transaction.getPayee());

        Mockito.when(transactionRepository.save(ArgumentMatchers.any()))
                .thenReturn(ArgumentMatchers.any());

        transactionService.createChargeback(transaction.getId());

        Mockito.verify(transactionRepository, Mockito.times(2))
                .save(transactionArgCaptor.capture());
        assertThat(transactionArgCaptor.getAllValues())
                .asList()
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("createdAt", "updatedAt")
                .contains(chargeback);
    }

    @Test
    void shouldReturnNotFoundExceptionWhenFindingTransactionById() {
        Long expectedTransactionId = 1L;

        Mockito.when(transactionRepository.findById(ArgumentMatchers.any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.findById(expectedTransactionId))
                .isExactlyInstanceOf(NotFoundException.class)
                .hasMessageContaining("Transaction " + expectedTransactionId + " not found.");
    }

    @Test
    void shouldReturnEmptyTransactionList() {
        Mockito.when(transactionRepository.findAll())
                .thenReturn(new ArrayList<>());

        assertThat(transactionService.findAll())
                .isEqualTo(List.of());
    }

    @Test
    void shouldReturnTransactionListWithThreeItems() {
        Mockito.when(transactionRepository.findAll())
                .thenReturn(List.of(
                        TransactionBuilder.buildTransactionWithId(1L),
                        TransactionBuilder.buildTransactionWithId(2L),
                        TransactionBuilder.buildTransactionWithId(3L)
                ));

        assertThat(transactionService.findAll())
                .asList()
                .size()
                .isEqualTo(3);
    }
}