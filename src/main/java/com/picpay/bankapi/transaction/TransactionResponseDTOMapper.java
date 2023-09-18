package com.picpay.bankapi.transaction;

import org.springframework.stereotype.Service;
import java.util.function.Function;

@Service
public class TransactionResponseDTOMapper implements Function<Transaction, TransactionResponseDTO> {

    @Override
    public TransactionResponseDTO apply(Transaction transaction) {
        return TransactionResponseDTO
                .builder()
                .id(transaction.getId())
                .value(transaction.getValue())
                .timestamp(transaction.getCreatedAt())
                .build();
    }
}
