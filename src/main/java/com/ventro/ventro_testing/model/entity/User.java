package com.ventro.ventro_testing.model.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    private UUID userId;
    private String firstName;
    private String lastName;
    private String image;
    private String email;
    private String password;
    private Boolean isFirstLogin;
    private String phoneNumber;
    private Boolean isActive;
    private String address;
    private String gender;
    private LocalDate dateOfBirth;
    private UUID branchId;        // ← add this
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Role role;
    private Branch branch;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.getRoleName()));
    }

    @Override public String getPassword()              { return password; }
    @Override public String getUsername()              { return email; }
    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(isActive);
    }
}