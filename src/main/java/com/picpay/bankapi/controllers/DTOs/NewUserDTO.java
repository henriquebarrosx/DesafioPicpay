package com.picpay.bankapi.controllers.DTOs;

import lombok.Data;

@Data
public class NewUserDTO {
    private String name;
    private String cpfCnpj;
    private String email;
    private String password;
}
