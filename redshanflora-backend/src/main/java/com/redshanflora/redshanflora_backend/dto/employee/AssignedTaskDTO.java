package com.redshanflora.redshanflora_backend.dto.employee;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignedTaskDTO {

    private Long orderId;
    private Long numberOfItems;
    private Long totalQuantity;

}
