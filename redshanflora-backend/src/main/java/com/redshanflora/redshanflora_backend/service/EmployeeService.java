package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.employee.AssignOrderRequestDTO;
import com.redshanflora.redshanflora_backend.dto.employee.AssignOrderResponseDTO;
import com.redshanflora.redshanflora_backend.dto.employee.AssignedEmployeeDTO;
import com.redshanflora.redshanflora_backend.dto.employee.NotAssignedEmployeeDTO;

import java.util.List;

public interface EmployeeService {

    List<NotAssignedEmployeeDTO> getNotAssignedEmployees();
    AssignOrderResponseDTO assignOrder(AssignOrderRequestDTO request);
    List<AssignedEmployeeDTO> getAssignedEmployees();

}