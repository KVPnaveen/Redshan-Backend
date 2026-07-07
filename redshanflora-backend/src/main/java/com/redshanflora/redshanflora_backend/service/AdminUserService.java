package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.admin.AdminUserRequest;
import com.redshanflora.redshanflora_backend.dto.admin.AdminUserResponse;

public interface AdminUserService {
    AdminUserResponse registerManager(AdminUserRequest request);
    AdminUserResponse registerEmployee(AdminUserRequest request);
}
