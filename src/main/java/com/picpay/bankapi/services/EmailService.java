package com.picpay.bankapi.services;

import com.picpay.bankapi.entities.Account;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {

    public void sendEmail(Account account, String message) {
        System.out.println(message);
    }

}
