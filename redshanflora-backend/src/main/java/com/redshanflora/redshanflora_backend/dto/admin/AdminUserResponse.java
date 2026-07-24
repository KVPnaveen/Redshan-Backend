package com.redshanflora.redshanflora_backend.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private Instant registeredDate;
    private String status;
    private Long managerId;
    private Long employeeId;
    private Long customerId;
    private Instant promoteDate;
}
