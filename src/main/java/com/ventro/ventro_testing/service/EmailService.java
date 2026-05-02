package com.ventro.ventro_testing.service;

public interface EmailService {
    void sendOtp(String toEmail, String otpCode);
    void sendAccountPassword(String toEmail, String password);  // ← add this
    void sendCredentials(String toEmail, String password);  // ← add this
}