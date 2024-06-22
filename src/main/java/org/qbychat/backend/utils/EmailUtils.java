package org.qbychat.backend.utils;

import jakarta.annotation.Resource;
import org.qbychat.backend.entity.VerifyEmail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;


public class EmailUtils {
    @Resource
    private JavaMailSenderImpl mailSender;

    @Value("${spring.email.username}")
    private String from;

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
