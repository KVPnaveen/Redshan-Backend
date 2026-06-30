package com.redshanflora.redshanflora_backend.service.impl;

import com.redshanflora.redshanflora_backend.dto.RegisterRequestDTO;
import com.redshanflora.redshanflora_backend.dto.RegisterResponseDTO;
import com.redshanflora.redshanflora_backend.entity.User;
import com.redshanflora.redshanflora_backend.enums.Role;
import com.redshanflora.redshanflora_backend.exception.EmailAlreadyExistsException;
import com.redshanflora.redshanflora_backend.repository.UserRepository;
import com.redshanflora.redshanflora_backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public RegisterResponseDTO register(RegisterRequestDTO request) {
        // 1. Normalize email
        String normalizedEmail = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : null;

        // 2. Proactive check for email existence
        if (normalizedEmail != null && userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        // 3. Build User entity (Role CUSTOMER is hardcoded here, registeredDate is handled by @PrePersist)
        User user = User.builder()
                .name(request.getName())
                .email(normalizedEmail)
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CUSTOMER)
                .build();

        User savedUser;
        try {
            // 4. Save User to database
            savedUser = userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            // 5. Fallback check for race conditions or unique constraint violations on email
            String rootMsg = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();
            if (rootMsg != null && rootMsg.toLowerCase().contains("email")) {
                throw new EmailAlreadyExistsException("Email already exists", ex);
            }
            throw ex;
        }

        // 5. Build and return the response DTO
        return RegisterResponseDTO.builder()
                .userId(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .phone(savedUser.getPhone())
                .role(savedUser.getRole().name())
                .registeredDate(savedUser.getRegisteredDate())
                .build();
    }
}
