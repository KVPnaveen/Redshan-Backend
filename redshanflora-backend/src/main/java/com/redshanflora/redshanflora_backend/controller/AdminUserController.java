package com.redshanflora.redshanflora_backend.controller;

import com.redshanflora.redshanflora_backend.dto.admin.AdminUserRequest;
import com.redshanflora.redshanflora_backend.dto.admin.AdminUserResponse;
import com.redshanflora.redshanflora_backend.dto.admin.AdminUserStatsResponse;
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

    @PostMapping("/register-employee")
    public ResponseEntity<AdminUserResponse> registerEmployee(@Valid @RequestBody AdminUserRequest request) {
        AdminUserResponse response = adminUserService.registerEmployee(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminUserStatsResponse> getUserStats() {
        return ResponseEntity.ok(adminUserService.getUserStats());
    }

    @GetMapping
    public ResponseEntity<java.util.List<AdminUserResponse>> getUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(adminUserService.getUsers(role, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminUserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminUserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody AdminUserRequest request) {
        return ResponseEntity.ok(adminUserService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
