package com.redshanflora.redshanflora_backend.controller;

import com.redshanflora.redshanflora_backend.dto.order.OrderDetailsDto;
import com.redshanflora.redshanflora_backend.dto.order.OrderSummaryDto;
import com.redshanflora.redshanflora_backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderSummaryDto>> getAllOrdersForAdmin() {
        log.info("Received GET request for admin/manager order list");
        List<OrderSummaryDto> orders = orderService.getAllOrdersForAdmin();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailsDto> getAdminOrderDetails(@PathVariable Long orderId) {
        log.info("Received GET request for admin/manager order details: orderId={}", orderId);
        OrderDetailsDto details = orderService.getAdminOrderDetails(orderId);
        return ResponseEntity.ok(details);
    }
}
