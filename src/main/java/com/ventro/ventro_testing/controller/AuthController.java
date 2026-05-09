package com.ventro.ventro_testing.controller;


import com.ventro.ventro_testing.config.SecurityUtils;
import com.ventro.ventro_testing.model.entity.User;
import com.ventro.ventro_testing.model.request.LoginRequest;
import com.ventro.ventro_testing.model.request.ResetPasswordRequest;
import com.ventro.ventro_testing.model.response.ApiResponse;
import com.ventro.ventro_testing.model.response.LoginResponse;
import com.ventro.ventro_testing.model.response.UserResponse;
import com.ventro.ventro_testing.repository.UserRepository;
import com.ventro.ventro_testing.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(security = { })
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody LoginRequest request) {

        LoginResponse loginResponse = authService.login(request);

        return ResponseEntity.ok(ApiResponse.<LoginResponse>builder()
                .success(true)
                .status(HttpStatus.OK)
                .message(loginResponse.getMessage())
                .data(loginResponse)
                .timestamp(Instant.now())
                .build());
    }

    @PostMapping("/resend-otp")
    @Operation(security = { })
    public ResponseEntity<ApiResponse<Void>> resendOtp(
            @RequestParam String email) {
        authService.sendResetOtp(email);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .status(HttpStatus.OK)
                .message("OTP resent to " + email)
                .timestamp(Instant.now())
                .build());
    }

    @PostMapping("/reset-password")
    @Operation(security = { })
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .status(HttpStatus.OK)
                .message("Password reset successfully. Please go to login again.")
                .timestamp(Instant.now())
                .build());
    }
    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        String email = SecurityUtils.getCurrentUser().getUsername();
        User user = userRepository.findByEmail(email);

        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .success(true)
                .status(HttpStatus.OK)
                .message("Profile retrieved successfully")
                .data(UserResponse.builder()
                        .userId(user.getUserId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .image(user.getImage())
                        .phoneNumber(user.getPhoneNumber())
                        .branchId(user.getBranchId())
                        .requiresPasswordChange(user.getRequiresPasswordChange())
                        .isActive(user.getIsActive())
                        .role(user.getRole().getRoleName())
                        .createdAt(user.getCreatedAt())
                        .build())
                .timestamp(Instant.now())
                .build());
    }


}