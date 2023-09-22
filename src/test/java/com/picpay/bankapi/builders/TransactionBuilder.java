package com.picpay.bankapi.builders;

import com.picpay.bankapi.entity.Account;
import com.picpay.bankapi.entity.Transaction;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.time.Month;

public class TransactionBuilder {

    public static Transaction buildTransactionWithId(Long id) {
        Account payer = AccountBuilder.buildCommonAccountTypeWithId(1L);
        Account payee = AccountBuilder.buildCommonAccountTypeWithId(2L);

        return Transaction
                .builder()
                .id(id)
                .value(BigDecimal.valueOf(10))
                .payer(payer)
                .payee(payee)
                .wasReversed(false)
                .isChargeback(false)
                .createdAt(LocalDateTime.of(2023, Month.SEPTEMBER, 1, 10, 0, 0))
                .updatedAt(LocalDateTime.of(2023, Month.SEPTEMBER, 1, 10, 0, 0))
                .build();
    }

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

    public static Transaction buildReversedFrom(Transaction transaction) {
        return Transaction
                .builder()
                .id(transaction.getId())
                .value(BigDecimal.valueOf(10))
                .payer(transaction.getPayer())
                .payee(transaction.getPayee())
                .wasReversed(true)
                .isChargeback(false)
                .createdAt(LocalDateTime.of(2023, Month.SEPTEMBER, 1, 10, 0, 0))
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Transaction buildChargebackTransactionWithId(Long id) {
        Account payer = AccountBuilder.buildCommonAccountTypeWithId(1L);
        Account payee = AccountBuilder.buildCommonAccountTypeWithId(2L);

        return Transaction
                .builder()
                .id(id)
                .value(BigDecimal.valueOf(10))
                .payer(payer)
                .payee(payee)
                .wasReversed(false)
                .isChargeback(true)
                .createdAt(LocalDateTime.of(2023, Month.SEPTEMBER, 1, 10, 0, 0))
                .updatedAt(LocalDateTime.of(2023, Month.SEPTEMBER, 1, 10, 0, 0))
                .build();
    }

    public static Transaction buildChargebackFrom(Transaction transaction) {
        // OBS: In chargeback, they change the transaction owner
        Account payer = transaction.getPayee();
        Account payee = transaction.getPayer();

        return Transaction
                .builder()
                .value(transaction.getValue())
                .payer(payer)
                .payee(payee)
                .wasReversed(false)
                .isChargeback(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
