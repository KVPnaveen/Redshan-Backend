package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.users.AuthResponse;
import com.redshanflora.redshanflora_backend.dto.users.LoginRequest;
import com.redshanflora.redshanflora_backend.dto.users.RegisterRequest;
import com.redshanflora.redshanflora_backend.dto.users.UserResponse;

public interface UserService {
    UserResponse registerUser(RegisterRequest registerRequest);
    AuthResponse loginUser(LoginRequest loginRequest);
}
