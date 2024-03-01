package dev.globalgoals.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Component
@Slf4j
public class MailManager {

    @Value("${spring.mail.username}")
    private String sender;

    @Autowired
    private JavaMailSender javaMailSender;

    public void send(String to,String sub,String con) throws MessagingException {
        //html 추가해서 보낼수 있음 (정교함)
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        mimeMessage.setFrom(sender);
        mimeMessage.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(to));
        mimeMessage.setSubject(sub);
        mimeMessage.setText(con);
        javaMailSender.send(mimeMessage);

    }
}