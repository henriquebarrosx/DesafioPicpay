package com.picpay.bankapi.builders;

import com.picpay.bankapi.entity.Account;
import com.picpay.bankapi.entity.Transaction;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.time.Month;

public class TransactionBuilder {

    public static Transaction buildStoredTransaction(Account payer, Account payee, BigDecimal amount) {
        payer.setBalance(payer.getBalance().subtract(amount));
        payee.setBalance(payee.getBalance().add(amount));

        return Transaction
                .builder()
                .id(1L)
                .value(amount)
                .payer(payer)
                .payee(payee)
                .wasReversed(false)
                .isChargeback(false)
                .createdAt(LocalDateTime.of(2023, Month.SEPTEMBER, 1, 10, 0, 0))
                .updatedAt(LocalDateTime.of(2023, Month.SEPTEMBER, 1, 10, 0, 0))
                .build();
    }

}
