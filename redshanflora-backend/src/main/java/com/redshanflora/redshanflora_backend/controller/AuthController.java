package com.redshanflora.redshanflora_backend.controller;

import com.redshanflora.redshanflora_backend.dto.users.AuthResponse;
import com.redshanflora.redshanflora_backend.dto.users.LoginRequest;
import com.redshanflora.redshanflora_backend.dto.users.RegisterRequest;
import com.redshanflora.redshanflora_backend.dto.users.UserResponse;
import com.redshanflora.redshanflora_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        UserResponse response = userService.registerUser(registerRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = userService.loginUser(loginRequest);
        return ResponseEntity.ok(response);
    }
}
