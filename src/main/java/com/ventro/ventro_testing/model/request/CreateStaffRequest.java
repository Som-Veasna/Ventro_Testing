package com.ventro.ventro_testing.model.request;

import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateStaffRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private String gender;
    private LocalDate dateOfBirth;
    private String roleName;
    private UUID branchId;
}