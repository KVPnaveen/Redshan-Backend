package com.redshanflora.redshanflora_backend.controller;

import com.redshanflora.redshanflora_backend.dto.OrderListDto;
import com.redshanflora.redshanflora_backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import com.redshanflora.redshanflora_backend.dto.order.CustomerOrderDto;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/all")
    public List<OrderListDto> getAllOrders() {
        return orderService.getAllOrders();
    }


    @GetMapping("/unassigned")
    public List<OrderListDto> getUnassignedOrders() {
        return orderService.getUnassignedOrders();
    }

    @GetMapping("/my-orders/{userId}")
    public ResponseEntity<List<CustomerOrderDto>> getMyOrders(@PathVariable Long userId) {
        List<CustomerOrderDto> orders = orderService.getCustomerOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }
}

