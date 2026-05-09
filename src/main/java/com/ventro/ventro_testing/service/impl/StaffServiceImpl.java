package com.ventro.ventro_testing.service.impl;

import com.ventro.ventro_testing.config.SecurityUtils;
import com.ventro.ventro_testing.model.entity.Role;
import com.ventro.ventro_testing.model.entity.User;
import com.ventro.ventro_testing.model.request.CreateStaffRequest;
import com.ventro.ventro_testing.model.request.UpdateStaffRequest;
import com.ventro.ventro_testing.model.response.UserResponse;
import com.ventro.ventro_testing.repository.StaffRepository;
import com.ventro.ventro_testing.repository.UserRepository;
import com.ventro.ventro_testing.service.EmailService;
import com.ventro.ventro_testing.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createStaff(CreateStaffRequest request) {
        if (userRepository.existsByEmail(request.getEmail()) > 0)
            throw new RuntimeException("Email already exists");

        String currentRole = SecurityUtils.getCurrentUser().getRole().getRoleName();
        String targetRole  = request.getRoleName();

        if (currentRole.equals("STAFF"))
            throw new RuntimeException("Staff does not have permission to create accounts");

        if (currentRole.equals("MANAGER") && !targetRole.equals("STAFF"))
            throw new RuntimeException("Manager can only create STAFF accounts");

        if (currentRole.equals("ADMIN") && !targetRole.equals("MANAGER") && !targetRole.equals("STAFF"))
            throw new RuntimeException("Admin can only create MANAGER or STAFF accounts");

        Role role = userRepository.findRoleByName(targetRole);
        if (role == null) throw new RuntimeException("Role not found: " + targetRole);

        String rawPassword;
        boolean sendEmail;

        if (targetRole.equals("MANAGER")) {
            rawPassword = generateRandomPassword();
            sendEmail   = true;
        } else {
            if (request.getPassword() == null || request.getPassword().isBlank())
                throw new RuntimeException("Password is required when creating STAFF");
            rawPassword = request.getPassword();
            sendEmail   = false;
        }
        UUID userId = UUID.randomUUID();
        staffRepository.insertStaff(
                User.builder()
                        .userId(userId)
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(rawPassword))
                        .phoneNumber(request.getPhoneNumber())
                        .branchId(request.getBranchId())
                        .requiresPasswordChange(true)
                        .isActive(true)
                        .role(role)
                        .build()
        );

        User saved = staffRepository.findById(userId);

        if (sendEmail) emailService.sendAccountPassword(saved.getEmail(), rawPassword);

        return toUserResponse(saved);
    }

    @Override
    public List<UserResponse> getAllStaff() {
        return staffRepository.findAllStaff()
                .stream()
                .map(this::toUserResponse)
                .toList();
    }

    @Override
    public UserResponse getStaffById(UUID userId) {
        User user = staffRepository.findById(userId);
        if (user == null) throw new RuntimeException("Staff not found");
        return toUserResponse(user);
    }

    @Override
    public UserResponse updateStaff(UUID userId, UpdateStaffRequest request) {
        User existing = staffRepository.findById(userId);
        if (existing == null) throw new RuntimeException("Staff not found");
        User updated = staffRepository.updateStaff(userId, request);
        return toUserResponse(updated);
    }

    @Override
    public void deleteStaff(UUID userId) {
        User existing = staffRepository.findById(userId);
        if (existing == null) throw new RuntimeException("Staff not found");
        staffRepository.deactivate(userId);
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
                .requiresPasswordChange(user.getRequiresPasswordChange())
                .isActive(user.getIsActive())
                .role(user.getRole().getRoleName())
                .createdAt(user.getCreatedAt())
                .build();
    }
}