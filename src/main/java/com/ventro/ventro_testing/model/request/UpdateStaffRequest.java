package com.ventro.ventro_testing.model.request;

import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStaffRequest {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String gender;
    private LocalDate dateOfBirth;
    private UUID branchId;
    private Boolean isActive;
}