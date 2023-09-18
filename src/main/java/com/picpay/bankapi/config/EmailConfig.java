package com.picpay.bankapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;

@Configuration
public class EmailConfig {

    @Bean
    SimpleMailMessage mailMessage() {
        return new SimpleMailMessage();
    }
}
