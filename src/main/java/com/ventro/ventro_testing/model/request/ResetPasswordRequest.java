
package com.ventro.ventro_testing.model.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {
    private String email;
    private String otp;
    private String newPassword;
}