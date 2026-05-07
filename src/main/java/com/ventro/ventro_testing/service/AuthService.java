package com.ventro.ventro_testing.service;
import com.ventro.ventro_testing.model.request.LoginRequest;
import com.ventro.ventro_testing.model.request.ResetPasswordRequest;
import com.ventro.ventro_testing.model.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    void sendResetOtp(String email);
    void resetPassword(ResetPasswordRequest request);

    void forgotPassword(String email);

    void resetForgotPassword(ResetPasswordRequest request);
}