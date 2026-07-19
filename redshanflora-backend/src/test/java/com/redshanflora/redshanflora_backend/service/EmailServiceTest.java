package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.ForgotPasswordRequestDto;
import com.redshanflora.redshanflora_backend.dto.VerifyOtpRequestDto;
import com.redshanflora.redshanflora_backend.dto.ResetPasswordRequestDto;
import com.redshanflora.redshanflora_backend.dto.ResetTokenResponseDto;
import com.redshanflora.redshanflora_backend.entity.PasswordResetOtp;
import com.redshanflora.redshanflora_backend.entity.User;
import com.redshanflora.redshanflora_backend.enums.Role;
import com.redshanflora.redshanflora_backend.exception.EmailDeliveryException;
import com.redshanflora.redshanflora_backend.exception.InvalidCredentialsException;
import com.redshanflora.redshanflora_backend.exception.OtpCooldownException;
import com.redshanflora.redshanflora_backend.repository.PasswordResetOtpRepository;
import com.redshanflora.redshanflora_backend.repository.UserRepository;
import com.redshanflora.redshanflora_backend.security.JwtService;
import com.redshanflora.redshanflora_backend.service.impl.PasswordResetServiceImpl;
import com.redshanflora.redshanflora_backend.service.impl.ProdEmailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetOtpRepository otpRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private PasswordResetServiceImpl passwordResetService;

    private ProdEmailServiceImpl prodEmailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        prodEmailService = new ProdEmailServiceImpl(mailSender);
        ReflectionTestUtils.setField(prodEmailService, "fromAddress", "no-reply@redshanflora.com");
    }
    @Test
    void testSmtpSuccess() {
        // Arrange
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act & Assert
        assertDoesNotThrow(() -> {
            prodEmailService.sendPasswordResetOtp("user@example.com", "123456");
        });
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSmtpAuthenticationFailure() {
        // Arrange
        doThrow(new MailAuthenticationException("SMTP Auth Failed")).when(mailSender).send(any(SimpleMailMessage.class));

        // Act & Assert
        EmailDeliveryException exception = assertThrows(EmailDeliveryException.class, () -> {
            prodEmailService.sendPasswordResetOtp("user@example.com", "123456");
        });
        assertTrue(exception.getMessage().contains("issue sending the verification email"));
    }

    @Test
    void testSmtpTimeout() {
        // Arrange
        doThrow(new MailSendException("SMTP Connection timed out")).when(mailSender).send(any(SimpleMailMessage.class));

        // Act & Assert
        EmailDeliveryException exception = assertThrows(EmailDeliveryException.class, () -> {
            prodEmailService.sendPasswordResetOtp("user@example.com", "123456");
        });
        assertTrue(exception.getMessage().contains("issue sending the verification email"));
    }

    @Test
    void testRequestOtp_RegisteredEmail_Success() {
        // Arrange
        String email = "registered@example.com";
        ForgotPasswordRequestDto request = new ForgotPasswordRequestDto(email);

        User user = User.builder()
                .id(1L)
                .email(email)
                .build();

        when(userRepository.findByEmailIgnoreCase(email.toLowerCase())).thenReturn(Optional.of(user));
        when(otpRepository.findFirstByUserOrderByCreatedAtDesc(user)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedOtp");

        // Act & Assert
        assertDoesNotThrow(() -> {
            passwordResetService.requestOtp(request);
        });

        verify(userRepository, times(1)).findByEmailIgnoreCase(email.toLowerCase());
        verify(otpRepository, times(1)).deleteByUser(user);
        verify(otpRepository, times(1)).save(any(PasswordResetOtp.class));
        verify(emailService, times(1)).sendPasswordResetOtp(eq(email.toLowerCase()), anyString());
    }

    @Test
    void testRequestOtp_UnknownEmail_GenericResponse() {
        // Arrange
        String email = "unknown@example.com";
        ForgotPasswordRequestDto request = new ForgotPasswordRequestDto(email);

        when(userRepository.findByEmailIgnoreCase(email.toLowerCase())).thenReturn(Optional.empty());

        // Act & Assert
        assertDoesNotThrow(() -> {
            // Must return without exceptions (generic response validation handled at controller)
            passwordResetService.requestOtp(request);
        });

        // Verifies no OTP is saved or email sent
        verify(userRepository, times(1)).findByEmailIgnoreCase(email.toLowerCase());
        verify(otpRepository, never()).deleteByUser(any());
        verify(otpRepository, never()).save(any());
        verify(emailService, never()).sendPasswordResetOtp(anyString(), anyString());
    }

    @Test
    void testRequestOtp_CooldownLimit() {
        // Arrange
        String email = "registered@example.com";
        ForgotPasswordRequestDto request = new ForgotPasswordRequestDto(email);

        User user = User.builder()
                .id(1L)
                .email(email)
                .build();

        PasswordResetOtp recentOtp = PasswordResetOtp.builder()
                .createdAt(Instant.now().minus(30, ChronoUnit.SECONDS))
                .build();

        when(userRepository.findByEmailIgnoreCase(email.toLowerCase())).thenReturn(Optional.of(user));
        when(otpRepository.findFirstByUserOrderByCreatedAtDesc(user)).thenReturn(Optional.of(recentOtp));

        // Act & Assert
        assertThrows(OtpCooldownException.class, () -> {
            passwordResetService.requestOtp(request);
        });

        verify(otpRepository, never()).deleteByUser(user);
    }

    @Test
    void testVerifyOtp_Success() {
        // Arrange
        String email = "registered@example.com";
        String plainOtp = "123456";
        String hashedOtp = "hashedOtp";
        VerifyOtpRequestDto request = new VerifyOtpRequestDto(email, plainOtp);

        User user = User.builder()
                .id(1L)
                .email(email)
                .build();

        PasswordResetOtp otpRecord = PasswordResetOtp.builder()
                .user(user)
                .otpHash(hashedOtp)
                .expiresAt(Instant.now().plus(5, ChronoUnit.MINUTES))
                .attemptCount(0)
                .build();

        when(userRepository.findByEmailIgnoreCase(email.toLowerCase())).thenReturn(Optional.of(user));
        when(otpRepository.findFirstByUserAndVerifiedFalseAndExpiresAtAfterOrderByCreatedAtDesc(eq(user), any(Instant.class)))
                .thenReturn(Optional.of(otpRecord));
        when(passwordEncoder.matches(plainOtp, hashedOtp)).thenReturn(true);
        when(jwtService.generatePasswordResetToken(user.getId())).thenReturn("validResetToken");

        // Act
        ResetTokenResponseDto response = passwordResetService.verifyOtp(request);

        // Assert
        assertNotNull(response);
        assertEquals("validResetToken", response.getResetToken());
        assertTrue(otpRecord.isVerified());
        verify(otpRepository, times(2)).save(otpRecord);
    }

    @Test
    void testVerifyOtp_Expired() {
        // Arrange
        String email = "registered@example.com";
        VerifyOtpRequestDto request = new VerifyOtpRequestDto(email, "123456");

        User user = User.builder()
                .id(1L)
                .email(email)
                .build();

        when(userRepository.findByEmailIgnoreCase(email.toLowerCase())).thenReturn(Optional.of(user));
        when(otpRepository.findFirstByUserAndVerifiedFalseAndExpiresAtAfterOrderByCreatedAtDesc(eq(user), any(Instant.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> {
            passwordResetService.verifyOtp(request);
        });
    }

    @Test
    void testVerifyOtp_MaxAttemptsExceeded() {
        // Arrange
        String email = "registered@example.com";
        String plainOtp = "123456";
        VerifyOtpRequestDto request = new VerifyOtpRequestDto(email, plainOtp);

        User user = User.builder()
                .id(1L)
                .email(email)
                .build();

        PasswordResetOtp otpRecord = PasswordResetOtp.builder()
                .user(user)
                .otpHash("hashedOtp")
                .expiresAt(Instant.now().plus(5, ChronoUnit.MINUTES))
                .attemptCount(5) // Hits limit on next try (6)
                .build();

        when(userRepository.findByEmailIgnoreCase(email.toLowerCase())).thenReturn(Optional.of(user));
        when(otpRepository.findFirstByUserAndVerifiedFalseAndExpiresAtAfterOrderByCreatedAtDesc(eq(user), any(Instant.class)))
                .thenReturn(Optional.of(otpRecord));

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> {
            passwordResetService.verifyOtp(request);
        });

        verify(otpRepository, times(1)).deleteByUser(user);
    }

    @Test
    void testResetPassword_Success() {
        // Arrange
        String resetToken = "validResetToken";
        ResetPasswordRequestDto request = new ResetPasswordRequestDto(resetToken, "NewPass123!", "NewPass123!");

        User user = User.builder()
                .id(1L)
                .email("user@example.com")
                .password("oldHashedPass")
                .build();

        PasswordResetOtp verifiedOtp = PasswordResetOtp.builder()
                .user(user)
                .verified(true)
                .expiresAt(Instant.now().plus(5, ChronoUnit.MINUTES))
                .build();

        when(jwtService.validatePasswordResetToken(resetToken)).thenReturn(true);
        when(jwtService.extractUserIdFromResetToken(resetToken)).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(otpRepository.findFirstByUserAndVerifiedTrueAndExpiresAtAfterOrderByCreatedAtDesc(eq(user), any(Instant.class)))
                .thenReturn(Optional.of(verifiedOtp));
        when(passwordEncoder.encode("NewPass123!")).thenReturn("newHashedPass");

        // Act & Assert
        assertDoesNotThrow(() -> {
            passwordResetService.resetPassword(request);
        });

        assertEquals("newHashedPass", user.getPassword());
        verify(userRepository, times(1)).save(user);
        verify(otpRepository, times(1)).deleteByUser(user);
    }

    @Test
    void testResetPassword_InvalidToken() {
        // Arrange
        String resetToken = "invalidResetToken";
        ResetPasswordRequestDto request = new ResetPasswordRequestDto(resetToken, "NewPass123!", "NewPass123!");

        when(jwtService.validatePasswordResetToken(resetToken)).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> {
            passwordResetService.resetPassword(request);
        });
    }

    @Test
    void testCaseInsensitiveEmailLookup() {
        // Arrange
        String lowercaseEmail = "user@example.com";
        String mixedcaseEmail = "User@Example.Com";

        User user = User.builder()
                .id(1L)
                .email(lowercaseEmail)
                .build();

        when(userRepository.findByEmailIgnoreCase(lowercaseEmail)).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userRepository.findByEmailIgnoreCase(mixedcaseEmail.toLowerCase());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(lowercaseEmail, result.get().getEmail());
    }
}
