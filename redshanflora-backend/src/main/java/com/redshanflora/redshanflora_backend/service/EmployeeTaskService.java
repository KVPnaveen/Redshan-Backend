package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.employee.AssignedTaskDTO;
import com.redshanflora.redshanflora_backend.dto.employee.EmployeeOrderItemDTO;
import com.redshanflora.redshanflora_backend.dto.employee.StockCheckResponseDTO;
import java.util.List;

public interface EmployeeTaskService {

    /**
     * Get all assigned orders of an employee
     */
    List<AssignedTaskDTO> getAssignedTasks(Long employeeId);

    /**
     * Get all order items of a selected order
     */
    List<EmployeeOrderItemDTO> getOrderItems(Long orderId);

    /**
     * Start processing an order
     */
    String startOrder(Long orderId);

    /**
     * Stop processing an order
     */
    String stopOrder(Long orderId);

    /**
     * Resume processing an order
     */
    String resumeOrder(Long orderId);

    /**
     * Complete processing an order
     */
    String completeOrder(Long orderId);

    StockCheckResponseDTO checkStock(Long orderItemId);


}