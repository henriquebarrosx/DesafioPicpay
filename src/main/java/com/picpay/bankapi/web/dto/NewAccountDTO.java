package com.picpay.bankapi.web.dto;

import com.picpay.bankapi.entity.AccountTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class NewAccountDTO {
    @NotNull
    private AccountTypeEnum type;

    @NotNull
    private String name;

    @NotNull
    private BigDecimal balance;

    @NotNull
    private String cpfCnpj;

    @NotNull
    private String email;

    @NotNull
    private String password;
}
