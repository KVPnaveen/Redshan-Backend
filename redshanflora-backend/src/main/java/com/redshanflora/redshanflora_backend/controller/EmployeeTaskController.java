package com.redshanflora.redshanflora_backend.controller;

import com.redshanflora.redshanflora_backend.dto.employee.AssignedTaskDTO;
import com.redshanflora.redshanflora_backend.dto.employee.EmployeeOrderItemDTO;
import com.redshanflora.redshanflora_backend.dto.employee.StockCheckResponseDTO;
import com.redshanflora.redshanflora_backend.service.EmployeeTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<AssignedTaskDTO>> getAssignedTasks(
            @PathVariable Long employeeId
    ) {

        List<AssignedTaskDTO> tasks =
                employeeTaskService.getAssignedTasks(employeeId);

        return ResponseEntity.ok(tasks);
    }


    /**
     * ============================================
     * Get Order Item Details
     * ============================================
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<EmployeeOrderItemDTO>> getOrderItems(
            @PathVariable Long orderId
    ) {

        return ResponseEntity.ok(
                employeeTaskService.getOrderItems(orderId)
        );
    }


    /**
     * ============================================
     * Start ONE Order Item
     *
     * Example:
     * PUT /api/employee/tasks/48/items/40/start
     * ============================================
     */
    @PutMapping("/{orderId}/items/{itemId}/start")
    public ResponseEntity<String> startItem(
            @PathVariable Long orderId,
            @PathVariable Long itemId
    ) {

        return ResponseEntity.ok(
                employeeTaskService.startItem(
                        orderId,
                        itemId
                )
        );
    }


    /**
     * ============================================
     * Stop ONE Order Item
     *
     * Example:
     * PUT /api/employee/tasks/48/items/40/stop
     * ============================================
     */
    @PutMapping("/{orderId}/items/{itemId}/stop")
    public ResponseEntity<String> stopItem(
            @PathVariable Long orderId,
            @PathVariable Long itemId
    ) {

        return ResponseEntity.ok(
                employeeTaskService.stopItem(
                        orderId,
                        itemId
                )
        );
    }


    /**
     * ============================================
     * Resume ONE Order Item
     *
     * Example:
     * PUT /api/employee/tasks/48/items/40/resume
     * ============================================
     */
    @PutMapping("/{orderId}/items/{itemId}/resume")
    public ResponseEntity<String> resumeItem(
            @PathVariable Long orderId,
            @PathVariable Long itemId
    ) {

        return ResponseEntity.ok(
                employeeTaskService.resumeItem(
                        orderId,
                        itemId
                )
        );
    }


    /**
     * ============================================
     * Complete ONE Order Item
     *
     * Example:
     * PUT /api/employee/tasks/48/items/40/complete
     *
     * IMPORTANT:
     * Only item 40 will be completed.
     * ============================================
     */
    @PutMapping("/{orderId}/items/{itemId}/complete")
    public ResponseEntity<String> completeItem(
            @PathVariable Long orderId,
            @PathVariable Long itemId
    ) {

        return ResponseEntity.ok(
                employeeTaskService.completeItem(
                        orderId,
                        itemId
                )
        );
    }


    /**
     * ============================================
     * Check Stock
     * ============================================
     */
    @GetMapping("/item/{orderItemId}/check-stock")
    public ResponseEntity<StockCheckResponseDTO> checkStock(
            @PathVariable Long orderItemId
    ) {

        return ResponseEntity.ok(
                employeeTaskService.checkStock(orderItemId)
        );
    }


    /**
     * ============================================
     * Get Completed Tasks
     * ============================================
     */
    @GetMapping("/completed/{employeeId}")
    public ResponseEntity<List<AssignedTaskDTO>> getCompletedTasks(
            @PathVariable Long employeeId
    ) {

        return ResponseEntity.ok(
                employeeTaskService.getCompletedTasks(employeeId)
        );
    }
}