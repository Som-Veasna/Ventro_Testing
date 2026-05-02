package com.ventro.ventro_testing.service;

import com.ventro.ventro_testing.model.request.CreateStaffRequest;
import com.ventro.ventro_testing.model.request.UpdateStaffRequest;
import com.ventro.ventro_testing.model.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface StaffService {
    UserResponse createStaff(CreateStaffRequest request);
    List<UserResponse> getAllStaff();
    UserResponse getStaffById(UUID userId);
    UserResponse updateStaff(UUID userId, UpdateStaffRequest request);
    void deleteStaff(UUID userId);
}