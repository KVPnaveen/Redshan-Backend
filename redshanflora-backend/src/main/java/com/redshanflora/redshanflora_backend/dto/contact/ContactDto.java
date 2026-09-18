package com.redshanflora.redshanflora_backend.dto.contact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactDto {
    private String fullName;
    private String email;
    private String phone;
    private String subject;
    private String message;
}
