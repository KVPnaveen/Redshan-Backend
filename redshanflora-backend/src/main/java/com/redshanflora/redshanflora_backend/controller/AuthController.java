package com.redshanflora.redshanflora_backend.controller;

import com.redshanflora.redshanflora_backend.dto.RegisterRequestDTO;
import com.redshanflora.redshanflora_backend.dto.RegisterResponseDTO;
import com.redshanflora.redshanflora_backend.dto.LoginRequestDTO;
import com.redshanflora.redshanflora_backend.dto.LoginResponseDTO;
import com.redshanflora.redshanflora_backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        RegisterResponseDTO response = authService.register(registerRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
}
