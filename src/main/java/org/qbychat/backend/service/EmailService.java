package org.qbychat.backend.service;

import jakarta.annotation.Resource;
import org.qbychat.backend.entity.VerifyEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;


@Service
public class EmailService {
    private final String from = "qby";
    @Autowired
    private JavaMailSenderImpl mailSender;

    public String sendVerifyEmail(VerifyEmail verifyEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(verifyEmail.getTo());
        message.setSubject(verifyEmail.getSubject());
        message.setText(verifyEmail.getContent());
        message.setFrom(from);
        try {
            mailSender.send(message);
            return "Succeed!";
        } catch (MailException e) {
            return e.getMessage();
        }
    }
}
