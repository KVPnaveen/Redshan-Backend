package com.redshanflora.redshanflora_backend.dto.employee;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignedEmployeeDTO {

    private Long employeeId;
    private String employeeName;
    private Long orderId;
    private String workingStatus;

}