package com.redshanflora.redshanflora_backend.service.impl;

import com.redshanflora.redshanflora_backend.dto.ForgotPasswordRequestDto;
import com.redshanflora.redshanflora_backend.dto.VerifyOtpRequestDto;
import com.redshanflora.redshanflora_backend.dto.ResetPasswordRequestDto;
import com.redshanflora.redshanflora_backend.dto.ResetTokenResponseDto;
import com.redshanflora.redshanflora_backend.entity.PasswordResetOtp;
import com.redshanflora.redshanflora_backend.entity.User;
import com.redshanflora.redshanflora_backend.exception.InvalidCredentialsException;
import com.redshanflora.redshanflora_backend.exception.OtpCooldownException;
import com.redshanflora.redshanflora_backend.exception.ResourceNotFoundException;
import com.redshanflora.redshanflora_backend.repository.PasswordResetOtpRepository;
import com.redshanflora.redshanflora_backend.repository.UserRepository;
import com.redshanflora.redshanflora_backend.security.JwtService;
import com.redshanflora.redshanflora_backend.service.EmailService;
import com.redshanflora.redshanflora_backend.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetOtpRepository otpRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void requestOtp(ForgotPasswordRequestDto request) {
        if (request.getEmail() == null) {
            return;
        }
        String normalizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);
        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(normalizedEmail);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // 1. Enforce 60-second cooldown
            Optional<PasswordResetOtp> latestOtpOpt = otpRepository.findFirstByUserOrderByCreatedAtDesc(user);
            if (latestOtpOpt.isPresent()) {
                PasswordResetOtp latestOtp = latestOtpOpt.get();
                Instant cooldownLimit = latestOtp.getCreatedAt().plus(60, ChronoUnit.SECONDS);
                if (Instant.now().isBefore(cooldownLimit)) {
                    long secondsRemaining = cooldownLimit.getEpochSecond() - Instant.now().getEpochSecond();
                    throw new OtpCooldownException("Please wait " + secondsRemaining + " seconds before requesting a new OTP.");
                }
            }

            // 2. Invalidate previous OTP records
            otpRepository.deleteByUser(user);

            // 3. Generate 6-digit numeric OTP
            int otpVal = 100000 + secureRandom.nextInt(900000);
            String plainOtp = String.valueOf(otpVal);

            // 4. Encode & Save OTP record
            PasswordResetOtp newOtp = PasswordResetOtp.builder()
                    .user(user)
                    .otpHash(passwordEncoder.encode(plainOtp))
                    .expiresAt(Instant.now().plus(5, ChronoUnit.MINUTES))
                    .verified(false)
                    .attemptCount(0)
                    .build();
            otpRepository.save(newOtp);

            // 5. Send OTP via Email Service
            emailService.sendPasswordResetOtp(normalizedEmail, plainOtp);
        } else {
            // Anti-enumeration protection: return success status, but log the event internally
            log.info("Password reset requested for unregistered email: {}", normalizedEmail);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResetTokenResponseDto verifyOtp(VerifyOtpRequestDto request) {
        if (request.getEmail() == null) {
            throw new InvalidCredentialsException("Invalid OTP or email");
        }
        String normalizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);
        User user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid OTP or email"));

        PasswordResetOtp otpRecord = otpRepository.findFirstByUserAndVerifiedFalseAndExpiresAtAfterOrderByCreatedAtDesc(user, Instant.now())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid OTP or email"));

        // Increment attempt counter
        otpRecord.setAttemptCount(otpRecord.getAttemptCount() + 1);
        otpRepository.save(otpRecord);

        // Enforce maximum of 5 validation attempts
        if (otpRecord.getAttemptCount() > 5) {
            otpRepository.deleteByUser(user);
            throw new InvalidCredentialsException("Maximum verification attempts exceeded. Please request a new OTP.");
        }

        // Verify match
        if (!passwordEncoder.matches(request.getOtp(), otpRecord.getOtpHash())) {
            throw new InvalidCredentialsException("Invalid OTP or email");
        }

        // Mark verified
        otpRecord.setVerified(true);
        otpRepository.save(otpRecord);

        // Generate short-lived reset JWT token using the immutable user ID
        String resetToken = jwtService.generatePasswordResetToken(user.getId());
        return new ResetTokenResponseDto(resetToken);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(ResetPasswordRequestDto request) {
        // Validate reset JWT
        if (!jwtService.validatePasswordResetToken(request.getResetToken())) {
            throw new InvalidCredentialsException("Invalid or expired reset token");
        }

        Long userId = jwtService.extractUserIdFromResetToken(request.getResetToken());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Ensure a verified OTP record exists and is not expired
        otpRepository.findFirstByUserAndVerifiedTrueAndExpiresAtAfterOrderByCreatedAtDesc(user, Instant.now())
                .orElseThrow(() -> new InvalidCredentialsException("Reset token verification failed or expired"));

        // Verify password confirmation match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Update database user password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Clear all user's OTP records
        otpRepository.deleteByUser(user);
    }
}
