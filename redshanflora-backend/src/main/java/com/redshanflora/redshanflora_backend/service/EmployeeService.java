package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.employee.NotAssignedEmployeeDTO;

import java.util.List;

public interface EmployeeService {

    List<NotAssignedEmployeeDTO> getNotAssignedEmployees();

}