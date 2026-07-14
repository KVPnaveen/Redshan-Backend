package com.redshanflora.redshanflora_backend.dto.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String password;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{9,15}$", message = "Phone number must be between 9 and 15 digits")
    private String phone;

    private String role;

    private String status;

    private Instant registeredDate;

    private Instant promoteDate;
}
