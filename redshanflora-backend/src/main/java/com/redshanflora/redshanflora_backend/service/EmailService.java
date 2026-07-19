package com.redshanflora.redshanflora_backend.service;

public interface EmailService {
    void sendPasswordResetOtp(String email, String otp);
}
