package com.redshanflora.redshanflora_backend.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeOrderItemDTO {

    private Long itemId;

    private String imageUrl;

    private String itemName;

    private Integer quantity;

    private Integer stockQuantity;

    private String status;

}
