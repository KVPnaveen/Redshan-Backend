package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.AuthResponse;
import com.redshanflora.redshanflora_backend.dto.LoginRequest;
import com.redshanflora.redshanflora_backend.dto.RegisterRequest;
import com.redshanflora.redshanflora_backend.dto.UserResponse;

public interface UserService {
    UserResponse registerUser(RegisterRequest registerRequest);
    AuthResponse loginUser(LoginRequest loginRequest);
}
