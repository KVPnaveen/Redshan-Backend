package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.LoginRequestDTO;
import com.redshanflora.redshanflora_backend.dto.LoginResponseDTO;
import com.redshanflora.redshanflora_backend.entity.User;
import com.redshanflora.redshanflora_backend.enums.Role;
import com.redshanflora.redshanflora_backend.exception.InvalidCredentialsException;
import com.redshanflora.redshanflora_backend.repository.UserRepository;
import com.redshanflora.redshanflora_backend.security.JwtService;
import com.redshanflora.redshanflora_backend.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceLoginTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogin_Success() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        String encodedPassword = "encodedPassword123";
        String token = "dummy-jwt-token";

        User user = User.builder()
                .id(1L)
                .name("Test User")
                .email(email)
                .password(encodedPassword)
                .role(Role.CUSTOMER)
                .build();

        LoginRequestDTO request = LoginRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn(token);

        // Act
        LoginResponseDTO response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals(token, response.getToken());
        assertEquals(1L, response.getUserId());
        assertEquals("Test User", response.getName());
        assertEquals(email, response.getEmail());
        assertEquals("CUSTOMER", response.getRole());

        verify(userRepository, times(1)).findByEmail(email.toLowerCase());
        verify(passwordEncoder, times(1)).matches(password, encodedPassword);
        verify(jwtService, times(1)).generateToken(user);
    }

    @Test
    void testLogin_InvalidEmail() {
        // Arrange
        String email = "nonexistent@example.com";
        LoginRequestDTO request = LoginRequestDTO.builder()
                .email(email)
                .password("password123")
                .build();

        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.empty());

        // Act & Assert
        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
            authService.login(request);
        });

        assertEquals("Invalid email or password", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(email.toLowerCase());
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void testLogin_InvalidPassword() {
        // Arrange
        String email = "test@example.com";
        String password = "wrongpassword";
        String encodedPassword = "encodedPassword123";

        User user = User.builder()
                .id(1L)
                .name("Test User")
                .email(email)
                .password(encodedPassword)
                .role(Role.CUSTOMER)
                .build();

        LoginRequestDTO request = LoginRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        // Act & Assert
        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
            authService.login(request);
        });

        assertEquals("Invalid email or password", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(email.toLowerCase());
        verify(passwordEncoder, times(1)).matches(password, encodedPassword);
        verify(jwtService, never()).generateToken(any());
    }
}
