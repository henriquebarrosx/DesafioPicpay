package com.picpay.bankapi.services;

import com.picpay.bankapi.entities.Account;
import com.picpay.bankapi.entities.Transaction;
import com.picpay.bankapi.enums.AccountTypeEnum;
import com.picpay.bankapi.exceptions.NotFoundException;
import com.picpay.bankapi.repositories.TransactionRepository;
import com.picpay.bankapi.controllers.DTOs.NewTransactionDTO;
import com.picpay.bankapi.exceptions.IllegalOperationException;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final EmailService emailService;

    public Transaction createTransaction(NewTransactionDTO params) {
        var payerAccount = accountService.findById(params.getPayerId());
        var payeeAccount = accountService.findById(params.getPayeeId());

        var transaction = Transaction
                .builder()
                .value(params.getValue())
                .payer(payerAccount)
                .payee(payeeAccount)
                .wasReversed(false)
                .isChargeback(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        validateTransaction(transaction, payerAccount, payeeAccount);
        accountService.subtractBalance(payerAccount, transaction.getValue());
        accountService.increaseBalance(payeeAccount, transaction.getValue());

        var emailMessage = "You received a transaction of " + parseTransactionToCurrency(transaction.getValue());
        emailService.sendEmail(payeeAccount, emailMessage);

        return transactionRepository.save(transaction);
    }

    String parseTransactionToCurrency(BigDecimal amount) {
        return NumberFormat.getCurrencyInstance().format(amount);
    }

    void validateTransaction(Transaction transaction, Account payerAccount, Account payeeAccount) {
        if (payerAccount.getId().equals(payeeAccount.getId())) {
            throw new IllegalOperationException("Payer and payee account should be different.");
        }

        if (payerAccount.getType().equals(AccountTypeEnum.SHOPKEEPER)) {
            throw new IllegalOperationException("Only common user type can send transactions.");
        }

        if (payerAccount.getBalance().compareTo(transaction.getValue()) < 0) {
            throw new IllegalOperationException("insufficient balance.");
        }
    }

    public void reverseTransaction(Long id) {
        var transaction = findById(id);

        if (transaction.getWasReversed().equals(true) || transaction.getIsChargeback().equals(true)) {
            throw new IllegalOperationException("The transaction cannot be reverted.");
        }

        transaction.setWasReversed(true);
        transaction.setUpdatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);

        var payerAccount = accountService.findById(transaction.getPayer().getId());
        var payeeAccount = accountService.findById(transaction.getPayee().getId());

        accountService.increaseBalance(payerAccount, transaction.getValue());
        accountService.subtractBalance(payeeAccount, transaction.getValue());
        createChargebackTransaction(transaction.getValue(), payeeAccount, payerAccount);
    }

    void createChargebackTransaction(BigDecimal amount, Account payer, Account payee) {
        var transaction = Transaction
                .builder()
                .value(amount)
                .payer(payer)
                .payee(payee)
                .isChargeback(true)
                .wasReversed(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);
    }

    public Transaction findById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Transaction %s not found.", id)));
    }
}
