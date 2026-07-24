package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.admin.AdminUserRequest;
import com.redshanflora.redshanflora_backend.dto.admin.AdminUserResponse;
import com.redshanflora.redshanflora_backend.dto.admin.AdminUserStatsResponse;

public interface AdminUserService {
    AdminUserResponse registerManager(AdminUserRequest request);
    AdminUserResponse registerEmployee(AdminUserRequest request);
    java.util.List<AdminUserResponse> getUsers(String role, String search);
    AdminUserResponse getUserById(Long id);
    AdminUserResponse updateUser(Long id, AdminUserRequest request);
    void deleteUser(Long id);

    AdminUserStatsResponse getUserStats();

}
