package com.redshanflora.redshanflora_backend.controller;

import com.redshanflora.redshanflora_backend.dto.OrderListDto;
import com.redshanflora.redshanflora_backend.dto.OrderStatusDTO;
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

    @GetMapping("/pre-orders")
    public ResponseEntity<List<OrderStatusDTO>> getPreOrders() {

        return ResponseEntity.ok(
                orderService.getPreOrders()
        );
    }

    @GetMapping("/processing")
    public ResponseEntity<List<OrderStatusDTO>> getProcessingOrders() {

        return ResponseEntity.ok(
                orderService.getProcessingOrders()
        );
    }

    @GetMapping("/completed")
    public ResponseEntity<List<OrderStatusDTO>> getCompletedOrders() {

        return ResponseEntity.ok(
                orderService.getCompletedOrders()
        );
    }

    @GetMapping("/dispatched")
    public ResponseEntity<List<OrderStatusDTO>> getDispatchedOrders() {

        return ResponseEntity.ok(
                orderService.getDispatchedOrders()
        );
    }

    // ============================================================
// DISPATCH ORDER TO COURIER
// ============================================================

    @PutMapping("/{orderId}/dispatch")
    public ResponseEntity<OrderStatusDTO> dispatchOrder(
            @PathVariable Long orderId
    ) {




        OrderStatusDTO response =
                orderService.dispatchOrder(orderId);





        return ResponseEntity.ok(response);
    }
}

