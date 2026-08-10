package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.employee.AssignedTaskDTO;
import com.redshanflora.redshanflora_backend.dto.employee.EmployeeOrderItemDTO;
import com.redshanflora.redshanflora_backend.dto.employee.StockCheckResponseDTO;

import java.util.List;

public interface EmployeeTaskService {

    /**
     * Get all assigned orders of an employee.
     *
     * Orders remain here until ALL their items
     * are completed.
     */
    List<AssignedTaskDTO> getAssignedTasks(Long employeeId);


    /**
     * Get all items belonging to an order.
     */
    List<EmployeeOrderItemDTO> getOrderItems(Long orderId);


    /**
     * Start ONE item.
     */
    String startItem(
            Long orderId,
            Long itemId
    );


    /**
     * Stop ONE item.
     */
    String stopItem(
            Long orderId,
            Long itemId
    );


    /**
     * Resume ONE item.
     */
    String resumeItem(
            Long orderId,
            Long itemId
    );


    /**
     * Complete ONE item.
     *
     * The order is completed ONLY when
     * every item in the order is completed.
     */
    String completeItem(
            Long orderId,
            Long itemId
    );


    /**
     * Check stock for one order item.
     */
    StockCheckResponseDTO checkStock(
            Long orderItemId
    );


    /**
     * Get orders which are completely finished.
     */
    List<AssignedTaskDTO> getCompletedTasks(
            Long employeeId
    );
}