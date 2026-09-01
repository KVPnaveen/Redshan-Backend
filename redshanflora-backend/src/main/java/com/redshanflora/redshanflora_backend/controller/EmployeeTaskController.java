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

    //------------------ ASSIGNED TASKS ---------------------------------------------

    @GetMapping("/assigned/normal/{employeeId}")
    public ResponseEntity<List<AssignedTaskDTO>> getNormalAssignedTasks(@PathVariable Long employeeId) {
        return ResponseEntity.ok(employeeTaskService.getNormalAssignedTasks(employeeId));
    }

    @GetMapping("/assigned/customized/{employeeId}")
    public ResponseEntity<List<AssignedTaskDTO>> getCustomizedAssignedTasks(@PathVariable Long employeeId) {
        return ResponseEntity.ok(employeeTaskService.getCustomizedAssignedTasks(employeeId));
    }

    //------------------ NORMAL ORDER ITEM DETAILS ----------------------------------

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<EmployeeOrderItemDTO>> getOrderItems(@PathVariable Long orderId) {
        return ResponseEntity.ok(employeeTaskService.getOrderItems(orderId));
    }

    //------------------ NORMAL ITEM ACTIONS ----------------------------------------
    // DO NOT CHANGE THESE.

    @PutMapping("/{orderId}/items/{itemId}/start")
    public ResponseEntity<String> startItem(@PathVariable Long orderId, @PathVariable Long itemId) {
        return ResponseEntity.ok(employeeTaskService.startItem(orderId, itemId));
    }

    @PutMapping("/{orderId}/items/{itemId}/stop")
    public ResponseEntity<String> stopItem(@PathVariable Long orderId, @PathVariable Long itemId) {
        return ResponseEntity.ok(employeeTaskService.stopItem(orderId, itemId));
    }

    @PutMapping("/{orderId}/items/{itemId}/resume")
    public ResponseEntity<String> resumeItem(@PathVariable Long orderId, @PathVariable Long itemId) {
        return ResponseEntity.ok(employeeTaskService.resumeItem(orderId, itemId));
    }

    @PutMapping("/{orderId}/items/{itemId}/complete")
    public ResponseEntity<String> completeItem(@PathVariable Long orderId, @PathVariable Long itemId) {
        return ResponseEntity.ok(employeeTaskService.completeItem(orderId, itemId));
    }

    //------------------ CUSTOMIZED ITEM ACTIONS ------------------------------------
    /*
     * IMPORTANT: customId = customized_bouquet.custom_id
     * These are separate from normal OrderItem APIs.
     */

    @PutMapping("/{orderId}/customized/{customId}/start")
    public ResponseEntity<String> startCustomizedItem(@PathVariable Long orderId, @PathVariable Long customId) {
        return ResponseEntity.ok(employeeTaskService.startCustomizedItem(orderId, customId));
    }

    @PutMapping("/{orderId}/customized/{customId}/stop")
    public ResponseEntity<String> stopCustomizedItem(@PathVariable Long orderId, @PathVariable Long customId) {
        return ResponseEntity.ok(employeeTaskService.stopCustomizedItem(orderId, customId));
    }

    @PutMapping("/{orderId}/customized/{customId}/resume")
    public ResponseEntity<String> resumeCustomizedItem(@PathVariable Long orderId, @PathVariable Long customId) {
        return ResponseEntity.ok(employeeTaskService.resumeCustomizedItem(orderId, customId));
    }

    @PutMapping("/{orderId}/customized/{customId}/complete")
    public ResponseEntity<String> completeCustomizedItem(@PathVariable Long orderId, @PathVariable Long customId) {
        return ResponseEntity.ok(employeeTaskService.completeCustomizedItem(orderId, customId));
    }

    //------------------ STOCK ------------------------------------------------------

    //------------------ COMPLETED TASKS --------------------------------------------

    @GetMapping("/completed/normal/{employeeId}")
    public ResponseEntity<List<AssignedTaskDTO>> getNormalCompletedTasks(@PathVariable Long employeeId) {
        return ResponseEntity.ok(employeeTaskService.getNormalCompletedTasks(employeeId));
    }

    @GetMapping("/completed/customized/{employeeId}")
    public ResponseEntity<List<AssignedTaskDTO>> getCustomizedCompletedTasks(@PathVariable Long employeeId) {
        return ResponseEntity.ok(employeeTaskService.getCustomizedCompletedTasks(employeeId));
    }
}