package com.redshanflora.redshanflora_backend.controller;

import com.redshanflora.redshanflora_backend.dto.ForgotPasswordRequestDto;
import com.redshanflora.redshanflora_backend.dto.VerifyOtpRequestDto;
import com.redshanflora.redshanflora_backend.dto.ResetPasswordRequestDto;
import com.redshanflora.redshanflora_backend.dto.ResetTokenResponseDto;
import com.redshanflora.redshanflora_backend.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/forgot-password")
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/request")
    public ResponseEntity<Map<String, String>> requestOtp(@Valid @RequestBody ForgotPasswordRequestDto request) {
        passwordResetService.requestOtp(request);
        return ResponseEntity.ok(Collections.singletonMap("message", "If an account exists for this email, a verification code has been sent."));
    }

    @PostMapping("/verify")
    public ResponseEntity<ResetTokenResponseDto> verifyOtp(@Valid @RequestBody VerifyOtpRequestDto request) {
        ResetTokenResponseDto response = passwordResetService.verifyOtp(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequestDto request) {
        passwordResetService.resetPassword(request);
        return ResponseEntity.ok(Collections.singletonMap("message", "Password reset successfully."));
    }
}
