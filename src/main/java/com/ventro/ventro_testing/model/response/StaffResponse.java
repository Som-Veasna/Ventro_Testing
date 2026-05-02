package com.ventro.ventro_testing.model.response;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffResponse {
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private String gender;
    private LocalDate dateOfBirth;
    private String role;
    private String branchName;
    private Boolean isFirstLogin;
    private Boolean isActive;
    private LocalDateTime createdAt;
}