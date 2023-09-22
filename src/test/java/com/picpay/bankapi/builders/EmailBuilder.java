package com.picpay.bankapi.builders;

import com.picpay.bankapi.entity.Account;
import com.picpay.bankapi.web.dto.EmailDTO;

import java.time.LocalDateTime;
import java.text.NumberFormat;
import java.math.BigDecimal;
import java.time.Month;
import java.time.ZoneId;
import java.util.Date;

public class EmailBuilder {

    public static EmailDTO buildEmailPayload(Account to, BigDecimal amount) {
        return EmailDTO
                .builder()
                .email(to.getEmail())
                .subject("New transaction received")
                .content("You received a transaction of " + NumberFormat.getCurrencyInstance().format(amount))
                .build();
    }

}
