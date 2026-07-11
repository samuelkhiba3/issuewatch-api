package com.IssueWatch.API.services;

import com.IssueWatch.API.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromMail;

    @Value("${app.mail.enabled}")
    private boolean mailEnabled;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(
            String to,
            String subject,
            String body
    ) {
        if (!mailEnabled) {
            System.out.println("EMAIL DISABLED");
            System.out.println("To: " + to);
            System.out.println("Subject: " + subject);
            System.out.println("Body: " + body);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(fromMail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);

        } catch (Exception exception) {
            throw new BadRequestException("Failed to send email");
        }
    }
}
