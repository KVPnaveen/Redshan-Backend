package com.redshanflora.redshanflora_backend.controller;

import com.redshanflora.redshanflora_backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.redshanflora.redshanflora_backend.dto.OrderListDto;
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<OrderListDto> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/unassigned")
    public List<OrderListDto> getUnassignedOrders() {
        return orderService.getUnassignedOrders();
    }
}
