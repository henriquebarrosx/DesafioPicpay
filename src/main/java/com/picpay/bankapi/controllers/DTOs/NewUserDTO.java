package com.picpay.bankapi.controllers.DTOs;

import com.picpay.bankapi.enums.AccountTypeEnum;
import lombok.Data;

@Data
public class NewUserDTO {
    private AccountTypeEnum type;
    private String name;
    private double balance;
    private String cpfCnpj;
    private String email;
    private String password;
}
