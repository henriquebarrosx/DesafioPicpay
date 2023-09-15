package com.picpay.bankapi.services;

import com.picpay.bankapi.entities.Transaction;
import com.picpay.bankapi.enums.AccountTypeEnum;
import com.picpay.bankapi.repositories.TransactionRepository;
import com.picpay.bankapi.controllers.DTOs.NewTransactionDTO;
import com.picpay.bankapi.exceptions.InvalidOperationException;

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

        if (payerAccount.getType().equals(AccountTypeEnum.SHOPKEEPER)) {
            throw new InvalidOperationException("Apenas usuário do tipo comum pode efetuar transferências");
        }

        if (payerAccount.getBalance() < params.getValue()) {
            throw new InvalidOperationException("Saldo insuficiente");
        }
        
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
}
