package com.picpay.bankapi.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.picpay.bankapi.web.dto.EmailDTO;
import org.springframework.stereotype.Service;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Service
@AllArgsConstructor
public class EmailService {
    private final JavaMailSender emailSender;

    @Async
    public void sendEmail(EmailDTO emailDTO) throws MailSendException {
        log.info("Sending new email: {}", emailDTO);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setSubject(emailDTO.getSubject());
        mailMessage.setText(emailDTO.getContent());
        mailMessage.setTo(emailDTO.getEmail());
        mailMessage.setSentDate(
                Date.from(
                        LocalDateTime.now()
                                .atZone(ZoneId.systemDefault())
                                .toInstant()
                )
        );

        emailSender.send(mailMessage);
    }
}
