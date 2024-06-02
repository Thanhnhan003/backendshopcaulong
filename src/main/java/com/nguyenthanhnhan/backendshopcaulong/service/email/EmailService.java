package com.nguyenthanhnhan.backendshopcaulong.service.email;


import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOrderConfirmationEmail(String toEmail, String subject, String body) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true);
            helper.setFrom("nguyenthanhnhan28092003@gmail.com", "NhanStore");

            mailSender.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            // Log the exception or handle it accordingly
            e.printStackTrace();
        }
    }

    public void sendActivationEmail(String toEmail, String activationLink) {
        String subject = "Account Activation";
        String body = "<p>Vui lòng nhấp vào liên kết sau để kích hoạt tài khoản của bạn:</p>"
                    + "<p><a href=\"" + activationLink + "\">Kích hoạt</a></p>";
        sendOrderConfirmationEmail(toEmail, subject, body);
    }
    
}