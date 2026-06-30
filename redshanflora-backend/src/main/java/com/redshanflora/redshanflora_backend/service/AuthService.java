package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.RegisterRequestDTO;
import com.redshanflora.redshanflora_backend.dto.RegisterResponseDTO;

public interface AuthService {
    RegisterResponseDTO register(RegisterRequestDTO request);
}
