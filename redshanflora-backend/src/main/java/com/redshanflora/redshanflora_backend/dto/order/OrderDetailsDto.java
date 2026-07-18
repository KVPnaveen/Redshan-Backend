package com.redshanflora.redshanflora_backend.dto.order;

import com.redshanflora.redshanflora_backend.dto.product.BouquetSnapshotDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailsDto {
    private Long orderId;
    private Instant orderDate;
    private BigDecimal totalAmount;
    private String orderStatus;
    private String paymentStatus;
    private String customerName;
    private String customerEmail;
    private List<OrderItemResponseDto> items;
    private BouquetSnapshotDto bouquetSnapshot;
}
