package com.financeapp.newsfeed.service;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${MAIL_FROM_ADDRESS}")
    private String fromAddress;

    public void sendNewsSummary(String toEmail, String subject, String summaryContent) throws MessagingException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(summaryContent);

        mailSender.send(message);
    }
}
