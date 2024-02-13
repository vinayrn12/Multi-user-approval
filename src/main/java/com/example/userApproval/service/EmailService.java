package com.example.userApproval.service;

import com.example.userApproval.dto.EmailDto;
import com.example.userApproval.exception.NotificationException;
import jakarta.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender emailSender;

    @Autowired
    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @SneakyThrows
    public void sendSimpleMessage(EmailDto emailDto) {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            helper.setTo(emailDto.getTo());
            helper.setSubject(emailDto.getSubject());
            helper.setText(emailDto.getText());
            /*emailSender.send(message);
                This is timing out due to auth issues and my account is not supporting the integration
                So just mocking the email integration for now
            */
            System.out.println("Mail sent to user: " + emailDto.getTo());
            System.out.println("Subject: " + emailDto.getSubject());
            System.out.println(emailDto.getText());
            System.out.println("=============");
        } catch (Exception e) {
            throw new NotificationException("Error sending mail to user");
        }
    }
}
