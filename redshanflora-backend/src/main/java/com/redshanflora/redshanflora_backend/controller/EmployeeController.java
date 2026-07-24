package com.redshanflora.redshanflora_backend.controller;

import com.redshanflora.redshanflora_backend.dto.employee.AssignOrderRequestDTO;
import com.redshanflora.redshanflora_backend.dto.employee.AssignOrderResponseDTO;
import com.redshanflora.redshanflora_backend.dto.employee.AssignedEmployeeDTO;
import com.redshanflora.redshanflora_backend.dto.employee.NotAssignedEmployeeDTO;
import com.redshanflora.redshanflora_backend.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/assign-order")
    public ResponseEntity<AssignOrderResponseDTO> assignOrder(
            @RequestBody AssignOrderRequestDTO request) {

        return ResponseEntity.ok(employeeService.assignOrder(request));
    }

    @GetMapping("/assigned")
    public ResponseEntity<List<AssignedEmployeeDTO>> getAssignedEmployees() {

        return ResponseEntity.ok(employeeService.getAssignedEmployees());
    }
}