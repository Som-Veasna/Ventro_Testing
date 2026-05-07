package com.ventro.ventro_testing.service;

public interface EmailService {
    void sendOtp(String toEmail, String otpCode);
    void sendAccountPassword(String toEmail, String password);

    void sendCredentials(String toEmail, String password);
}