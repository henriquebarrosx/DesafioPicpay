package com.picpay.bankapi.services;

import com.picpay.bankapi.entities.Transaction;
import com.picpay.bankapi.enums.AccountTypeEnum;
import com.picpay.bankapi.exceptions.NotFoundException;
import com.picpay.bankapi.repositories.TransactionRepository;
import com.picpay.bankapi.controllers.DTOs.NewTransactionDTO;
import com.picpay.bankapi.exceptions.IllegalOperationException;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    
    public Transaction create(NewTransactionDTO params) {
        var payerAccount = accountService.findById(params.getPayerId());
        var payeeAccount = accountService.findById(params.getPayeeId());

        if (payerAccount.getId().equals(payeeAccount.getId())) {
            throw new IllegalOperationException("A conta de origem e destino devem ser diferentes");
        }

        if (payerAccount.getType().equals(AccountTypeEnum.SHOPKEEPER)) {
            throw new IllegalOperationException("Apenas usuário do tipo comum pode efetuar transferências");
        }

        if (payerAccount.getBalance() < params.getValue()) {
            throw new IllegalOperationException("Saldo insuficiente");
        }

        payerAccount.setBalance(payerAccount.getBalance() - params.getValue());
        accountService.update(payerAccount);

        payeeAccount.setBalance(payeeAccount.getBalance() + params.getValue());
        accountService.update(payeeAccount);

        var transaction = Transaction
                .builder()
                .value(params.getValue())
                .payer(payerAccount)
                .payee(payeeAccount)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        return transactionRepository.save(transaction);
    }

    public Boolean reverse(Long id) {
        var transaction = findById(id);

        if (transaction.getIsReverted().equals(true)) {
            throw new IllegalOperationException("Transferência não pode ser extornada novamente");
        }

        transaction.setIsReverted(true);
        transaction.setUpdatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);

        var payerAccount = accountService.findById(transaction.getPayer().getId());
        payerAccount.setBalance(payerAccount.getBalance() + transaction.getValue());
        accountService.update(payerAccount);

        var payeeAccount = accountService.findById(transaction.getPayee().getId());
        payeeAccount.setBalance(payeeAccount.getBalance() - transaction.getValue());
        accountService.update(payeeAccount);

        return true;
    }

    public Transaction findById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transação com id " + id + " não encontrada"));
    }
}
