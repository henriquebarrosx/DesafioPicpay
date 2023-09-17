package com.picpay.bankapi.transaction;

import com.picpay.bankapi.account.Account;
import com.picpay.bankapi.email.EmailService;
import com.picpay.bankapi.account.AccountService;
import com.picpay.bankapi.account.AccountTypeEnum;
import com.picpay.bankapi.exception.NotFoundException;
import com.picpay.bankapi.exception.IllegalOperationException;

import java.util.List;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionBuilder transactionBuilder;
    private final AccountService accountService;
    private final EmailService emailService;

    public Transaction createTransaction(NewTransactionDTO params) {
        var payerAccount = accountService.findById(params.getPayerId());
        var payeeAccount = accountService.findById(params.getPayeeId());

        var transaction = transactionBuilder.buildDefaultTransaction(params.getValue(), payerAccount, payeeAccount);
        validateTransactionCreation(transaction, payerAccount, payeeAccount);

        accountService.subtractBalance(payerAccount, transaction.getValue());
        accountService.increaseBalance(payeeAccount, transaction.getValue());

        var emailMessage = "You received a transaction of " + parseTransactionValueToCurrency(transaction.getValue());
        emailService.sendEmail(payeeAccount, emailMessage);

        return transactionRepository.save(transaction);
    }

    String parseTransactionValueToCurrency(BigDecimal amount) {
        return NumberFormat.getCurrencyInstance().format(amount);
    }

    public void reverseTransaction(Long id) {
        var transaction = getReversedTransaction(findById(id));

        if (transaction.getWasReversed().equals(true) || transaction.getIsChargeback().equals(true)) {
            throw new IllegalOperationException("The transaction cannot be reverted.");
        }

        var payerAccount = accountService.findById(transaction.getPayer().getId());
        var payeeAccount = accountService.findById(transaction.getPayee().getId());
        var chargeback = transactionBuilder.buildChargebackTransaction(transaction.getValue(), payeeAccount, payerAccount);

        accountService.increaseBalance(payerAccount, transaction.getValue());
        accountService.subtractBalance(payeeAccount, transaction.getValue());
        transactionRepository.saveAll(List.of(transaction, chargeback));
    }

    public Transaction findById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Transaction %s not found.", id)));
    }

    void validateTransactionCreation(Transaction transaction, Account payerAccount, Account payeeAccount) {
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

    Transaction getReversedTransaction(Transaction transaction) {
        transaction.setWasReversed(true);
        transaction.setUpdatedAt(LocalDateTime.now());
        return transaction;
    }
}
