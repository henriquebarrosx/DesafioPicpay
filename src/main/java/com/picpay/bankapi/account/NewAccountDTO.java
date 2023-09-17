package com.picpay.bankapi.account;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class NewAccountDTO {
    private AccountTypeEnum type;
    private String name;
    private BigDecimal balance;
    private String cpfCnpj;
    private String email;
    private String password;
}
