package com.ventro.ventro_testing.service.impl;

import com.ventro.ventro_testing.jwt.JwtService;
import com.ventro.ventro_testing.model.entity.User;
import com.ventro.ventro_testing.model.request.LoginRequest;
import com.ventro.ventro_testing.model.request.ResetPasswordRequest;
import com.ventro.ventro_testing.model.response.LoginResponse;
import com.ventro.ventro_testing.repository.UserRepository;
import com.ventro.ventro_testing.service.AuthService;
import com.ventro.ventro_testing.service.EmailService;
import com.ventro.ventro_testing.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;

    private static final String OTP_PREFIX        = "reset-otp:";
    private static final String FORGOT_OTP_PREFIX = "forgot-otp:";

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()));

        User user = (User) authentication.getPrincipal();
        String role = user.getRole().getRoleName();
        if (role.equals("STAFF")) {
            return LoginResponse.builder()
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .role(role)
                    .requiresPasswordChange(false)
                    .token(jwtService.generateToken(user))
                    .message("Login successful")
                    .build();
        }
        if (Boolean.TRUE.equals(user.getRequiresPasswordChange())) {
            sendResetOtp(user.getEmail());
            return LoginResponse.builder()
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .role(role)
                    .requiresPasswordChange(true)
                    .token(null)
                    .message("First login detected. OTP sent to your email. Please reset your password.")
                    .build();
        }
        return LoginResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(role)
                .requiresPasswordChange(false)
                .token(jwtService.generateToken(user))
                .message("Login successful")
                .build();
    }

    @Override
    public void sendResetOtp(String email) {
        String otpCode = String.format("%06d", new Random().nextInt(999999));
        redisService.save(OTP_PREFIX + email, otpCode, 5);
        emailService.sendOtp(email, otpCode);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        Object storedOtp = redisService.get(OTP_PREFIX + request.getEmail());
        if (storedOtp == null || !storedOtp.toString().equals(request.getOtp()))
            throw new RuntimeException("Invalid or expired OTP");

        userRepository.resetPassword(
                request.getEmail(),
                passwordEncoder.encode(request.getNewPassword())
        );
        redisService.delete(OTP_PREFIX + request.getEmail());
    }

    @Override
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new RuntimeException("Email not found");

        String otpCode = String.format("%06d", new Random().nextInt(999999));
        redisService.save(FORGOT_OTP_PREFIX + email, otpCode, 5);
        emailService.sendOtp(email, otpCode);
    }

    @Override
    public void resetForgotPassword(ResetPasswordRequest request) {
        Object storedOtp = redisService.get(FORGOT_OTP_PREFIX + request.getEmail());
        if (storedOtp == null || !storedOtp.toString().equals(request.getOtp()))
            throw new RuntimeException("Invalid or expired OTP");

        userRepository.resetPassword(
                request.getEmail(),
                passwordEncoder.encode(request.getNewPassword())
        );
        redisService.delete(FORGOT_OTP_PREFIX + request.getEmail());
    }
}