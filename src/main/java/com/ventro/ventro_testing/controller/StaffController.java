package com.ventro.ventro_testing.controller;

import com.ventro.ventro_testing.model.request.CreateStaffRequest;
import com.ventro.ventro_testing.model.request.UpdateStaffRequest;
import com.ventro.ventro_testing.model.response.ApiResponse;
import com.ventro.ventro_testing.model.response.UserResponse;
import com.ventro.ventro_testing.service.StaffService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/staffs")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class StaffController {

    private final StaffService staffService;

    // ADMIN and MANAGER can create — but MANAGER can only create STAFF
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<UserResponse>> createStaff(
            @RequestBody CreateStaffRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<UserResponse>builder()
                        .success(true)
                        .status(HttpStatus.CREATED)
                        .message("Staff created successfully")
                        .data(staffService.createStaff(request))
                        .timestamp(Instant.now())
                        .build());
    }
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllStaff() {
        return ResponseEntity.ok(
                ApiResponse.<List<UserResponse>>builder()
                        .success(true)
                        .status(HttpStatus.OK)
                        .message("Staff retrieved successfully")
                        .data(staffService.getAllStaff())
                        .timestamp(Instant.now())
                        .build());
    }
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<UserResponse>> getStaffById(
            @PathVariable UUID userId) {
        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .success(true)
                        .status(HttpStatus.OK)
                        .message("Staff retrieved successfully")
                        .data(staffService.getStaffById(userId))
                        .timestamp(Instant.now())
                        .build());
    }
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateStaff(
            @PathVariable UUID userId,
            @RequestBody UpdateStaffRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .success(true)
                        .status(HttpStatus.OK)
                        .message("Staff updated successfully")
                        .data(staffService.updateStaff(userId, request))
                        .timestamp(Instant.now())
                        .build());
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteStaff(
            @PathVariable UUID userId) {
        staffService.deleteStaff(userId);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .status(HttpStatus.OK)
                        .message("Staff deactivated successfully")
                        .timestamp(Instant.now())
                        .build());
    }
}