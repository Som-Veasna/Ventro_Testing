package com.ventro.ventro_testing.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    private UUID userId;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private Boolean requiresPasswordChange;
    private String token;
    private String message;
}