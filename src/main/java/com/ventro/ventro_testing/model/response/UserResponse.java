package com.ventro.ventro_testing.model.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private String image;
    private String phoneNumber;
    private UUID branchId;
    private Boolean requiresPasswordChange;
    private Boolean isActive;
    private String role;
    private LocalDateTime createdAt;
}