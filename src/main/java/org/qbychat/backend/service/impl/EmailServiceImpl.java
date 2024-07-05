package org.qbychat.backend.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.qbychat.backend.entity.Email;
import org.qbychat.backend.mapper.EmailMapper;
import org.qbychat.backend.service.EmailService;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;


@Service
@Log4j2
public class EmailServiceImpl extends ServiceImpl<EmailMapper, Email> implements EmailService {
    protected final String from = "qby";

    @Resource
    JavaMailSenderImpl mailSender;

    public String sendVerifyEmail(Email verifyEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(verifyEmail.getTo());
        message.setSubject(verifyEmail.getSubject());
        message.setText(verifyEmail.getContent());
        message.setFrom(from);
        try {
            log.info("Sending email to {}", verifyEmail.getTo());
            mailSender.send(message);
            log.info("Email sent");
            return "Succeed!";
        } catch (MailException e) {
            return e.getMessage();
        }
    }
}
