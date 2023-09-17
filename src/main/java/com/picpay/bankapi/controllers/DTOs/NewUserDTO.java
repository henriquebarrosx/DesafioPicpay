package com.picpay.bankapi.controllers.DTOs;

import com.picpay.bankapi.enums.AccountTypeEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class NewUserDTO {
    private AccountTypeEnum type;
    private String name;
    private BigDecimal balance;
    private String cpfCnpj;
    private String email;
    private String password;
}
