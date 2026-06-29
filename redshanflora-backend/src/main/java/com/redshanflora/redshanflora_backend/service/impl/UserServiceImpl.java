package com.redshanflora.redshanflora_backend.service.impl;

import com.redshanflora.redshanflora_backend.dto.users.AuthResponse;
import com.redshanflora.redshanflora_backend.dto.users.LoginRequest;
import com.redshanflora.redshanflora_backend.dto.users.RegisterRequest;
import com.redshanflora.redshanflora_backend.dto.users.UserResponse;
import com.redshanflora.redshanflora_backend.entity.Customer;
import com.redshanflora.redshanflora_backend.enums.Role;
import com.redshanflora.redshanflora_backend.entity.User;
import com.redshanflora.redshanflora_backend.exception.EmailAlreadyExistsException;
import com.redshanflora.redshanflora_backend.exception.InvalidCredentialsException;
import com.redshanflora.redshanflora_backend.repository.CustomerRepository;
import com.redshanflora.redshanflora_backend.repository.UserRepository;
import com.redshanflora.redshanflora_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Email already in use");
        }

        // 1. Build and save the main User entity
        User user = User.builder()
                .name(registerRequest.getFirstName() + " " + registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .phone(registerRequest.getPhone())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.CUSTOMER) // Default role is CUSTOMER
                .build();

        User savedUser = userRepository.save(user);

        // 2. Build and save the associated Customer entity
        Customer customer = Customer.builder()
                .user(savedUser)
                .address(registerRequest.getAddress())
                .loyaltyPoints(0)
                .build();
        
        Customer savedCustomer = customerRepository.save(customer);
        savedUser.setCustomer(savedCustomer); // link back in-memory for response mapping

        return mapToUserResponse(savedUser);
    }

    @Override
    public AuthResponse loginUser(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        UserResponse userResponse = mapToUserResponse(user);

        return AuthResponse.builder()
                .message("Login successful")
                .token("dummy-jwt-token-for-future-use") // Placeholder for future JWT implementation
                .user(userResponse)
                .build();
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .role(user.getRole())
                .createdAt(user.getRegisteredDate())
                .build();
    }
}
