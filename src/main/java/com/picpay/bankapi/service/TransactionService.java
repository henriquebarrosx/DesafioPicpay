package com.picpay.bankapi.service;

import java.text.NumberFormat;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.picpay.bankapi.web.dto.EmailDTO;
import lombok.AllArgsConstructor;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.picpay.bankapi.entity.Account;
import com.picpay.bankapi.entity.Transaction;
import com.picpay.bankapi.web.dto.TransactionDTO;
import com.picpay.bankapi.entity.AccountTypeEnum;
import com.picpay.bankapi.exception.NotFoundException;
import com.picpay.bankapi.web.mapper.TransactionDTOMapper;
import com.picpay.bankapi.repository.TransactionRepository;
import com.picpay.bankapi.exception.IllegalOperationException;

@Slf4j
@Service
@AllArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionDTOMapper transactionDTOMapper;
    private final AccountService accountService;
    private final EmailService emailService;

    public Transaction createTransaction(BigDecimal amount, Long payerId, Long payeeId) {
        var payerAccount = accountService.findById(payerId);
        var payeeAccount = accountService.findById(payeeId);

        var transaction = buildDefaulTransaction(amount, payerAccount, payeeAccount);
        validateTransactionRegistration(transaction, payerAccount, payeeAccount);
        log.info("Creating transaction: {}", transaction);

        accountService.subtractBalance(payerAccount, transaction.getValue());
        accountService.increaseBalance(payeeAccount, transaction.getValue());
        Transaction savedTransaction = transactionRepository.save(transaction);
        emailService.sendEmail(buildNewTransactionReceivedEmailDTO(transaction));
        return savedTransaction;
    }

    public Transaction createChargeback(Long transactionId) {
        var transaction = findById(transactionId);
        validateIfAlreadyBeenReversed(transaction);

        var payerAccount = accountService.findById(transaction.getPayer().getId());
        var payeeAccount = accountService.findById(transaction.getPayee().getId());

        var chargeback = buildChargeback(transaction.getValue(), payeeAccount, payerAccount);
        log.info("Creating chargeback: {}", chargeback);

        blockNewChargebackRequests(transaction);
        accountService.increaseBalance(payerAccount, transaction.getValue());
        accountService.subtractBalance(payeeAccount, transaction.getValue());
        transactionRepository.save(chargeback);

        return chargeback;
    }

    public Transaction findById(Long transactionId) {
        log.info("Finding transaction by id: {}", transactionId);
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NotFoundException(String.format("Transaction %s not found.", transactionId)));
    }

    public List<Transaction> findAll() {
        log.info("Finding all transactions");
        return transactionRepository.findAll();
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

    private EmailDTO buildNewTransactionReceivedEmailDTO(Transaction transaction) {
        return EmailDTO.builder()
                .email(transaction.getPayee().getEmail())
                .subject("New transaction received")
                .content("You received a transaction of " + NumberFormat.getCurrencyInstance().format(transaction.getValue()))
                .build();
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
