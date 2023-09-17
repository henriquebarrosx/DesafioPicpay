package com.picpay.bankapi.transaction;

import com.picpay.bankapi.account.Account;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class TransactionBuilder {

    public Transaction buildDefaultTransaction(BigDecimal amount, Account payerAccount, Account payeeAccount) {
        return Transaction
                .builder()
                .value(amount)
                .payer(payerAccount)
                .payee(payeeAccount)
                .wasReversed(false)
                .isChargeback(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

    }

    public Transaction buildChargebackTransaction(BigDecimal amount, Account payer, Account payee) {
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
