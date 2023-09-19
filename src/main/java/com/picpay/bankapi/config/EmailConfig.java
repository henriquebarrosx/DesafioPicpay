package com.picpay.bankapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

    private static final String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";
    private static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
    private static final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
    private static final String MAIL_DEBUG = "mail.debug";
    public static final String MAIL_DEBUG_FLAG = "false";

    @Value("${spring.mail.properties.mail.host}")
    private String host;

    @Value("${spring.mail.properties.mail.smtp.port}")
    private Integer port;

    @Value("${spring.mail.properties.mail.username}")
    private String username;

    @Value("${spring.mail.properties.mail.password}")
    private String password;

    @Value("${spring.mail.properties.mail.transport.protocol}")
    private String protocol;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private Boolean auth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enabled}")
    private Boolean isStarttlsEnabled;

    @Bean
    JavaMailSender mailMessage() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);

        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties properties = mailSender.getJavaMailProperties();
        properties.put(MAIL_TRANSPORT_PROTOCOL, protocol);
        properties.put(MAIL_SMTP_AUTH, auth);
        properties.put(MAIL_SMTP_STARTTLS_ENABLE, isStarttlsEnabled);
        properties.put(MAIL_DEBUG, MAIL_DEBUG_FLAG);

        return mailSender;
    }
}
