package com.redshanflora.redshanflora_backend.controller;

import com.redshanflora.redshanflora_backend.dto.employee.AssignedTaskDTO;
import com.redshanflora.redshanflora_backend.dto.employee.EmployeeOrderItemDTO;
import com.redshanflora.redshanflora_backend.service.EmployeeTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.redshanflora.redshanflora_backend.dto.employee.StockCheckResponseDTO;
import java.util.List;

@RestController
@RequestMapping("/api/employee/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmployeeTaskController {

    private final EmployeeTaskService employeeTaskService;

    /**
     * ============================================
     * Get Assigned Tasks
     * ============================================
     */
    @GetMapping("/assigned/{employeeId}")
    public ResponseEntity<List<AssignedTaskDTO>>
    getAssignedTasks(
            @PathVariable Long employeeId
    ) {


        List<AssignedTaskDTO> tasks =
                employeeTaskService
                        .getAssignedTasks(
                                employeeId
                        );


        return ResponseEntity.ok(tasks);

    }

    /**
     * ============================================
     * Get Order Item Details
     * ============================================
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<EmployeeOrderItemDTO>> getOrderItems(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                employeeTaskService.getOrderItems(orderId)
        );

    }

    /**
     * ============================================
     * Start Order
     * ============================================
     */
    @PutMapping("/{orderId}/start")
    public ResponseEntity<String> startOrder(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                employeeTaskService.startOrder(orderId)
        );

    }

    /**
     * ============================================
     * Stop Order
     * ============================================
     */
    @PutMapping("/{orderId}/stop")
    public ResponseEntity<String> stopOrder(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                employeeTaskService.stopOrder(orderId)
        );

    }

    /**
     * ============================================
     * Resume Order
     * ============================================
     */
    @PutMapping("/{orderId}/resume")
    public ResponseEntity<String> resumeOrder(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                employeeTaskService.resumeOrder(orderId)
        );

    }

    /**
     * ============================================
     * Complete Order
     * ============================================
     */
    @PutMapping("/{orderId}/complete")
    public ResponseEntity<String> completeOrder(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                employeeTaskService.completeOrder(orderId)
        );

    }

    @GetMapping("/item/{orderItemId}/check-stock")
    public ResponseEntity<?> checkStock(
            @PathVariable Long orderItemId) {

        return ResponseEntity.ok(
                employeeTaskService.checkStock(orderItemId)
        );
    }



}