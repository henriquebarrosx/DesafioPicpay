package com.picpay.bankapi.web.mapper;

import com.picpay.bankapi.web.dto.NewAccountDTO;
import com.picpay.bankapi.entity.Account;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.function.Function;

@Service
public class NewAccountDTOMapper implements Function<NewAccountDTO, Account> {
    @Override
    public Account apply(NewAccountDTO newAccountDTO) {
        return Account
                .builder()
                .name(newAccountDTO.getName())
                .email(newAccountDTO.getEmail())
                .cpfCnpj(newAccountDTO.getCpfCnpj())
                .password(newAccountDTO.getPassword())
                .type(newAccountDTO.getType())
                .balance(newAccountDTO.getBalance())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
