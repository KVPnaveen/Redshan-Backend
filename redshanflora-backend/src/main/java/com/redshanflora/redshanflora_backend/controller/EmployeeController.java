package com.redshanflora.redshanflora_backend.controller;

import com.redshanflora.redshanflora_backend.dto.employee.NotAssignedEmployeeDTO;
import com.redshanflora.redshanflora_backend.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/not-assigned")
    public ResponseEntity<List<NotAssignedEmployeeDTO>> getNotAssignedEmployees() {

        return ResponseEntity.ok(employeeService.getNotAssignedEmployees());

    }
}