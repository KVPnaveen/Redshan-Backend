package com.redshanflora.redshanflora_backend.service.impl;

import com.redshanflora.redshanflora_backend.dto.employee.NotAssignedEmployeeDTO;
import com.redshanflora.redshanflora_backend.entity.Employee;
import com.redshanflora.redshanflora_backend.repository.EmployeeRepository;
import com.redshanflora.redshanflora_backend.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public List<NotAssignedEmployeeDTO> getNotAssignedEmployees() {

        List<Employee> employees = employeeRepository.findByStatus("Not Assigned");

        return employees.stream()
                .map(employee -> NotAssignedEmployeeDTO.builder()
                        .employeeId(employee.getId())
                        .userId(employee.getUser().getId())
                        .employeeName(employee.getUser().getName())
                        .build())
                .collect(Collectors.toList());
    }
}