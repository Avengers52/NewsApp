package com.financeapp.newsfeed.service;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendNewsSummary(String toEmail, String subject, String summaryContent) throws MessagingException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("bikashshah15b@gmail.com");
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(summaryContent);

        mailSender.send(message);
    }
}
