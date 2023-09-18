package com.picpay.bankapi.transaction;

import com.picpay.bankapi.account.Account;
import com.picpay.bankapi.email.EmailService;
import com.picpay.bankapi.account.AccountService;
import com.picpay.bankapi.account.AccountTypeEnum;
import com.picpay.bankapi.exception.NotFoundException;
import com.picpay.bankapi.exception.IllegalOperationException;

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

    public Transaction createTransaction(TransactionDTO newTransactionParams) {
        var payerAccount = accountService.findById(newTransactionParams.payerId());
        var payeeAccount = accountService.findById(newTransactionParams.payeeId());

        var transaction = buildDefaulTransaction(newTransactionParams.value(), payerAccount, payeeAccount);
        validateTransactionRegistration(transaction, payerAccount, payeeAccount);

        accountService.subtractBalance(payerAccount, transaction.getValue());
        accountService.increaseBalance(payeeAccount, transaction.getValue());

        var emailMessage = "You received a transaction of " + parseTransactionValueToCurrency(transaction.getValue());
        emailService.sendEmail(payeeAccount, emailMessage);

        return transactionRepository.save(transaction);
    }

    String parseTransactionValueToCurrency(BigDecimal amount) {
        return NumberFormat.getCurrencyInstance().format(amount);
    }

    public Transaction createChargeback(Long transactionTargetId) {
        var transactionTarget = findById(transactionTargetId);
        validateIfAlreadyBeenReversed(transactionTarget);

        var payerAccount = accountService.findById(transactionTarget.getPayer().getId());
        var payeeAccount = accountService.findById(transactionTarget.getPayee().getId());

        var chargeback = buildChargeback(transactionTarget.getValue(), payeeAccount, payerAccount);
        transactionRepository.save(chargeback);

        blockNewChargebackRequests(transactionTarget);

        accountService.increaseBalance(payerAccount, transactionTarget.getValue());
        accountService.subtractBalance(payeeAccount, transactionTarget.getValue());

        return chargeback;
    }

    public Transaction findById(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NotFoundException(String.format("Transaction %s not found.", transactionId)));
    }

    private void validateTransactionRegistration(Transaction transaction, Account payerAccount, Account payeeAccount) {
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

    private void validateIfAlreadyBeenReversed(Transaction transaction) {
        if (transaction.getWasReversed().equals(true)) {
            throw new IllegalOperationException("Transaction only can be reverted once.");
        }

        if (transaction.getIsChargeback().equals(true)) {
            throw new IllegalOperationException("The transaction is not able to be reversed.");
        }
    }

    private void blockNewChargebackRequests(Transaction transaction) {
        transaction.setWasReversed(true);
        transaction.setUpdatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    private Transaction buildDefaulTransaction(BigDecimal amount, Account payer, Account payee) {
        return Transaction
                .builder()
                .value(amount)
                .payer(payer)
                .payee(payee)
                .wasReversed(false)
                .isChargeback(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private Transaction buildChargeback(BigDecimal amount, Account payer, Account payee) {
        return Transaction
                .builder()
                .value(amount)
                .payer(payer)
                .payee(payee)
                .isChargeback(true)
                .wasReversed(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
