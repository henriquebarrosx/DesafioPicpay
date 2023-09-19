package com.picpay.bankapi.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.picpay.bankapi.web.dto.EmailDTO;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.mail.javamail.JavaMailSender;

@Slf4j
@Service
@AllArgsConstructor
public class EmailService {
    private final JavaMailSender emailSender;

    @Async
    public void sendEmail(EmailDTO emailDTO) {
        try {
            log.info("Sending new email: {}", emailDTO);
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setSubject(emailDTO.getSubject());
            mailMessage.setText(emailDTO.getContent());
            mailMessage.setSentDate(emailDTO.getSendDate());
            mailMessage.setTo(emailDTO.getEmail());
            emailSender.send(mailMessage);
        }

        catch (Exception ex) {
            log.error("Error sending new email: {}", ex.getMessage());
        }
    }
}
