package org.qbychat.backend.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.qbychat.backend.entity.Email;
import org.qbychat.backend.mapper.EmailMapper;
import org.qbychat.backend.service.EmailService;
import org.qbychat.backend.utils.QMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;


@Service
public class EmailServiceImpl extends ServiceImpl<EmailMapper, Email> implements EmailService {
    protected final String from = "qby";

    @Autowired
    QMailSender mailSender;

    public String sendVerifyEmail(Email verifyEmail) {
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
