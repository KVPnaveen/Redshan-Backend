package com.redshanflora.redshanflora_backend.dto.employee;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignOrderRequestDTO {

    private Long employeeId;
    private Long orderId;

}