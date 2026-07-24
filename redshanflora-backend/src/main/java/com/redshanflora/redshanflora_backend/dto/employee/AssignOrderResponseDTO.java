package com.redshanflora.redshanflora_backend.dto.employee;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignOrderResponseDTO {

    private Long employeeId;
    private String employeeName;

    private Long orderId;

    private String message;

}