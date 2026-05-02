package com.ventro.ventro_testing.service.impl;

import com.ventro.ventro_testing.jwt.JwtService;
import com.ventro.ventro_testing.model.entity.User;
import com.ventro.ventro_testing.model.request.LoginRequest;
import com.ventro.ventro_testing.model.request.ResetPasswordRequest;
import com.ventro.ventro_testing.model.response.LoginResponse;
import com.ventro.ventro_testing.model.response.UserResponse;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;

    private static final String OTP_PREFIX = "reset-otp:";

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()));

        User user = (User) authentication.getPrincipal();
        String role = user.getRole().getRoleName();

        // STAFF — return token directly
        if (role.equals("STAFF")) {
            return LoginResponse.builder()
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .role(role)
                    .isFirstLogin(user.getIsFirstLogin())
                    .token(jwtService.generateToken(user))
                    .message("Login successful")
                    .build();
        }

        // ADMIN or MANAGER — first login, send OTP
        if (Boolean.TRUE.equals(user.getIsFirstLogin())) {
            sendResetOtp(user.getEmail());
            return LoginResponse.builder()
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .role(role)
                    .isFirstLogin(true)
                    .token(null)
                    .message("First login detected. OTP sent to your email. Please reset your password.")
                    .build();
        }

        // ADMIN or MANAGER — not first login, return token
        return LoginResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(role)
                .isFirstLogin(false)
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

    private String generateRandomPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .image(user.getImage())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .gender(user.getGender())
                .branchId(user.getBranchId())
                .dateOfBirth(user.getDateOfBirth())
                .isFirstLogin(user.getIsFirstLogin())
                .isActive(user.getIsActive())
                .role(user.getRole().getRoleName())
                .createdAt(user.getCreatedAt())
                .build();
    }
}