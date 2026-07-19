package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.ForgotPasswordRequestDto;
import com.redshanflora.redshanflora_backend.dto.VerifyOtpRequestDto;
import com.redshanflora.redshanflora_backend.dto.ResetPasswordRequestDto;
import com.redshanflora.redshanflora_backend.dto.ResetTokenResponseDto;

public interface PasswordResetService {
    void requestOtp(ForgotPasswordRequestDto request);
    ResetTokenResponseDto verifyOtp(VerifyOtpRequestDto request);
    void resetPassword(ResetPasswordRequestDto request);
}
