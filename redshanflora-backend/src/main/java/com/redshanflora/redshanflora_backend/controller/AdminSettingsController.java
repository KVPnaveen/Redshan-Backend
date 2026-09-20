package com.redshanflora.redshanflora_backend.controller;

import com.redshanflora.redshanflora_backend.dto.admin.AdminPasswordChangeDto;
import com.redshanflora.redshanflora_backend.dto.admin.AdminProfileDto;
import com.redshanflora.redshanflora_backend.entity.Admin;
import com.redshanflora.redshanflora_backend.entity.User;
import com.redshanflora.redshanflora_backend.enums.Role;
import com.redshanflora.redshanflora_backend.exception.EmailAlreadyExistsException;
import com.redshanflora.redshanflora_backend.exception.ResourceNotFoundException;
import com.redshanflora.redshanflora_backend.repository.AdminRepository;
import com.redshanflora.redshanflora_backend.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/settings")
@RequiredArgsConstructor
@CrossOrigin
public class AdminSettingsController {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    private User resolveAdminUser() {
        List<User> adminUsers = userRepository.findByRole(Role.ADMIN);
        if (adminUsers == null || adminUsers.isEmpty()) {
            throw new ResourceNotFoundException("No user with role ADMIN found");
        }
        if (adminUsers.size() > 1) {
            throw new IllegalStateException("Multiple ADMIN users found in system. Cannot resolve unique admin profile without userId.");
        }
        return adminUsers.get(0);
    }

    private AdminProfileDto buildDto(User user) {
        Admin admin = adminRepository.findByUser_Id(user.getId())
                .orElse(null);

        return AdminProfileDto.builder()
                .adminId(admin != null ? admin.getId() : null)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .build();
    }

    @GetMapping
    public ResponseEntity<AdminProfileDto> getAdminSettings() {
        User user = resolveAdminUser();
        return ResponseEntity.ok(buildDto(user));
    }

    @PutMapping(params = "!action")
    public ResponseEntity<AdminProfileDto> updateAdminSettings(@Valid @RequestBody AdminProfileDto requestDto) {
        if (requestDto.getName() == null || requestDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name must not be empty");
        }
        if (requestDto.getEmail() == null || requestDto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email must not be empty");
        }

        User user = resolveAdminUser();
        return updateUserProfile(user, requestDto);
    }

    @PutMapping(params = "action=password")
    public ResponseEntity<Map<String, String>> changeAdminPassword(@Valid @RequestBody AdminPasswordChangeDto requestDto) {
        if (requestDto.getCurrentPassword() == null || requestDto.getCurrentPassword().isBlank()) {
            throw new IllegalArgumentException("Current password is required");
        }
        if (requestDto.getNewPassword() == null || requestDto.getNewPassword().isBlank()) {
            throw new IllegalArgumentException("New password is required");
        }
        if (requestDto.getConfirmPassword() == null || requestDto.getConfirmPassword().isBlank()) {
            throw new IllegalArgumentException("Confirm password is required");
        }
        if (!requestDto.getNewPassword().equals(requestDto.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }
        if (requestDto.getNewPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }

        User adminUser = resolveAdminUser();

        if (!passwordEncoder.matches(requestDto.getCurrentPassword(), adminUser.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        adminUser.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
        userRepository.save(adminUser);

        return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<AdminProfileDto> getAdminSettingsByUserId(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (user.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("User with ID " + userId + " is not an ADMIN");
        }

        return ResponseEntity.ok(buildDto(user));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<AdminProfileDto> updateAdminSettingsByUserId(
            @PathVariable Long userId,
            @Valid @RequestBody AdminProfileDto requestDto) {

        if (requestDto.getName() == null || requestDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name must not be empty");
        }
        if (requestDto.getEmail() == null || requestDto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email must not be empty");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (user.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("User with ID " + userId + " is not an ADMIN");
        }

        return updateUserProfile(user, requestDto);
    }

    private ResponseEntity<AdminProfileDto> updateUserProfile(User user, AdminProfileDto requestDto) {
        String newEmail = requestDto.getEmail().trim();
        if (!user.getEmail().equalsIgnoreCase(newEmail)) {
            userRepository.findByEmailIgnoreCase(newEmail).ifPresent(existingUser -> {
                if (!existingUser.getId().equals(user.getId())) {
                    throw new EmailAlreadyExistsException("Email already in use: " + newEmail);
                }
            });
        }

        user.setName(requestDto.getName().trim());
        user.setEmail(newEmail);
        user.setPhone(requestDto.getPhone() != null ? requestDto.getPhone().trim() : null);

        User updatedUser = userRepository.save(user);

        return ResponseEntity.ok(buildDto(updatedUser));
    }
}
