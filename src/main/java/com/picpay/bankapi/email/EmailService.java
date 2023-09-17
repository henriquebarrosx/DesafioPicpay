package com.picpay.bankapi.email;

import com.picpay.bankapi.account.Account;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {

    public void sendEmail(Account account, String message) {
        System.out.println(message);
    }

}
