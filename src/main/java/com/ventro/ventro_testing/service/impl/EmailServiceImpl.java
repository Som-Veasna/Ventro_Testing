package com.ventro.ventro_testing.service.impl;

import com.ventro.ventro_testing.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendOtp(String toEmail, String otpCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("Your OTP Verification Code");
            helper.setText(loadTemplate(otpCode), true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    private String loadTemplate(String otpCode) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/otp-email.html");
            String html = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            return html
                    .replace("{{D1}}", String.valueOf(otpCode.charAt(0)))
                    .replace("{{D2}}", String.valueOf(otpCode.charAt(1)))
                    .replace("{{D3}}", String.valueOf(otpCode.charAt(2)))
                    .replace("{{D4}}", String.valueOf(otpCode.charAt(3)))
                    .replace("{{D5}}", String.valueOf(otpCode.charAt(4)))
                    .replace("{{D6}}", String.valueOf(otpCode.charAt(5)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load email template: " + e.getMessage());
        }
    }
    @Override
    public void sendAccountPassword(String toEmail, String password) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("Your Account Credentials");
            helper.setText(buildCredentialsHtml(toEmail, password), true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    private String buildCredentialsHtml(String email, String password) {
        return """
        <!DOCTYPE html>
        <html>
        <body style="margin:0; padding:0; font-family: Arial, sans-serif; background-color: #f9f9f9;">
            <div style="max-width: 480px; margin: 40px auto; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 20px rgba(0,0,0,0.1);">
                <div style="background: linear-gradient(135deg, #f8a4a4, #a8b4f8); padding: 30px; text-align: center;">
                    <h1 style="color: white; margin: 0; font-size: 22px;">Your App Name</h1>
                </div>
                <div style="background: white; padding: 40px; text-align: center;">
                    <h2 style="color: #F4845F;">Your Account is Ready</h2>
                    <p style="color: #666;">Here are your login credentials:</p>
                    <div style="background:#f5f5f5; border-radius:10px; padding:20px; margin:20px 0; text-align:left;">
                        <p style="margin:5px 0;"><strong>Email:</strong> %s</p>
                        <p style="margin:5px 0;"><strong>Password:</strong> %s</p>
                    </div>
                    <p style="color:#888; font-size:13px;">
                        Please login and change your password immediately.
                    </p>
                </div>
            </div>
        </body>
        </html>
    """.formatted(email, password);
    }
    @Override
    public void sendCredentials(String toEmail, String password) {
        sendAccountPassword(toEmail, password);
    }

}