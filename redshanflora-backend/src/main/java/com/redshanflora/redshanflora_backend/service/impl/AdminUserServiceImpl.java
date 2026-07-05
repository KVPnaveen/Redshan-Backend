package com.redshanflora.redshanflora_backend.service.impl;

import com.redshanflora.redshanflora_backend.dto.admin.AdminUserRequest;
import com.redshanflora.redshanflora_backend.dto.admin.AdminUserResponse;
import com.redshanflora.redshanflora_backend.entity.Manager;
import com.redshanflora.redshanflora_backend.entity.User;
import com.redshanflora.redshanflora_backend.enums.Role;
import com.redshanflora.redshanflora_backend.exception.EmailAlreadyExistsException;
import com.redshanflora.redshanflora_backend.repository.ManagerRepository;
import com.redshanflora.redshanflora_backend.repository.UserRepository;
import com.redshanflora.redshanflora_backend.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final ManagerRepository managerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AdminUserResponse registerManager(AdminUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already in use");
        }

        // 1. Build and save the main User entity with Role MANAGER
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.MANAGER)
                .build();

        User savedUser = userRepository.save(user);

        // 2. Build and save the associated Manager entity
        Manager manager = Manager.builder()
                .user(savedUser)
                .promoteDate(Instant.now())
                .build();

        Manager savedManager = managerRepository.save(manager);

        // 3. Return the response DTO
        return AdminUserResponse.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .phone(savedUser.getPhone())
                .role(savedUser.getRole().name())
                .registeredDate(savedUser.getRegisteredDate())
                .managerId(savedManager.getId())
                .promoteDate(savedManager.getPromoteDate())
                .build();
    }
}
