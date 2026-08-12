package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.employee.AssignedTaskDTO;
import com.redshanflora.redshanflora_backend.dto.employee.EmployeeOrderItemDTO;
import com.redshanflora.redshanflora_backend.dto.employee.StockCheckResponseDTO;

import java.util.List;

public interface EmployeeTaskService {

    // =========================================================
    // ASSIGNED TASKS
    // =========================================================

    List<AssignedTaskDTO> getNormalAssignedTasks(
            Long employeeId
    );

    List<AssignedTaskDTO> getCustomizedAssignedTasks(
            Long employeeId
    );


    // =========================================================
    // ORDER ITEMS
    // =========================================================

    List<EmployeeOrderItemDTO> getOrderItems(
            Long orderId
    );


    // =========================================================
    // ITEM STATUS
    // =========================================================

    String startItem(
            Long orderId,
            Long itemId
    );

    String stopItem(
            Long orderId,
            Long itemId
    );

    String resumeItem(
            Long orderId,
            Long itemId
    );

    String completeItem(
            Long orderId,
            Long itemId
    );


    // =========================================================
    // STOCK
    // =========================================================

    StockCheckResponseDTO checkStock(
            Long orderItemId
    );


    // =========================================================
    // COMPLETED TASKS
    // =========================================================

    List<AssignedTaskDTO> getNormalCompletedTasks(
            Long employeeId
    );

    List<AssignedTaskDTO> getCustomizedCompletedTasks(
            Long employeeId
    );

    // =========================================================
// CUSTOMIZED ITEM WORKING STATUS
// =========================================================

    String startCustomizedItem(
            Long orderId,
            Long customId
    );

    String stopCustomizedItem(
            Long orderId,
            Long customId
    );

    String resumeCustomizedItem(
            Long orderId,
            Long customId
    );

    String completeCustomizedItem(
            Long orderId,
            Long customId
    );
}