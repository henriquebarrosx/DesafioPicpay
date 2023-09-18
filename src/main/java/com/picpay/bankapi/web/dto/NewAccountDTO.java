package com.picpay.bankapi.web.dto;

import com.picpay.bankapi.entity.AccountTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class NewAccountDTO {
    @NotNull
    @NotBlank
    private AccountTypeEnum type;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    private BigDecimal balance;
    @NotNull
    @NotBlank
    private String cpfCnpj;
    @NotNull
    @NotBlank
    private String email;
    @NotNull
    @NotBlank
    private String password;
}
