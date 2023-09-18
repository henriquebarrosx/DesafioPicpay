package com.picpay.bankapi.web.mapper;

import com.picpay.bankapi.entity.Transaction;
import com.picpay.bankapi.web.dto.TransactionResponseDTO;
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
