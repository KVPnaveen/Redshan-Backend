package com.redshanflora.redshanflora_backend.controller;

import com.redshanflora.redshanflora_backend.dto.admin.AdminUserRequest;
import com.redshanflora.redshanflora_backend.dto.admin.AdminUserResponse;
import com.redshanflora.redshanflora_backend.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @PostMapping("/register-manager")
    public ResponseEntity<AdminUserResponse> registerManager(@Valid @RequestBody AdminUserRequest request) {
        AdminUserResponse response = adminUserService.registerManager(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
