package com.picpay.bankapi.web.mapper;

import com.picpay.bankapi.entity.Transaction;
import com.picpay.bankapi.web.dto.TransactionDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@AllArgsConstructor
public class TransactionDTOMapper implements Function<Transaction, TransactionDTO> {
    private final AccountDTOMapper accountDTOMapper;

    @Override
    public TransactionDTO apply(Transaction transaction) {
        return new TransactionDTO(
                transaction.getId(),
                accountDTOMapper.apply(transaction.getPayer()),
                accountDTOMapper.apply(transaction.getPayee()),
                transaction.getValue(),
                transaction.getIsChargeback()
        );
    }
}
