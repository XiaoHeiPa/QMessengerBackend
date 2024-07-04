package org.qbychat.backend.utils;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class QMailSender extends JavaMailSenderImpl {
    public QMailSender() {
        this.setHost("smtp-mail.outlook.com");
        this.setPort(587);
        this.setUsername("zxysb1145@outlook.com");
        this.setPassword("qwer1145");
        this.setDefaultEncoding("UTF-8");

        Properties props = this.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
    }
}
