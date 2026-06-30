package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.RegisterRequestDTO;
import com.redshanflora.redshanflora_backend.dto.RegisterResponseDTO;
import com.redshanflora.redshanflora_backend.dto.LoginRequestDTO;
import com.redshanflora.redshanflora_backend.dto.LoginResponseDTO;

public interface AuthService {
    RegisterResponseDTO register(RegisterRequestDTO request);
    LoginResponseDTO login(LoginRequestDTO request);
}

