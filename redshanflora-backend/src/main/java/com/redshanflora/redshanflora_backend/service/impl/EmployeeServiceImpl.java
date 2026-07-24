package com.redshanflora.redshanflora_backend.service.impl;

import com.redshanflora.redshanflora_backend.dto.employee.AssignOrderRequestDTO;
import com.redshanflora.redshanflora_backend.dto.employee.AssignOrderResponseDTO;
import com.redshanflora.redshanflora_backend.dto.employee.AssignedEmployeeDTO;
import com.redshanflora.redshanflora_backend.dto.employee.NotAssignedEmployeeDTO;
import com.redshanflora.redshanflora_backend.entity.Employee;
import com.redshanflora.redshanflora_backend.entity.Order;
import com.redshanflora.redshanflora_backend.repository.EmployeeRepository;
import com.redshanflora.redshanflora_backend.repository.OrderRepository;
import com.redshanflora.redshanflora_backend.service.EmployeeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final OrderRepository orderRepository;

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

    @Override
    @Transactional
    public AssignOrderResponseDTO assignOrder(AssignOrderRequestDTO request) {

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        // Prevent assigning an already assigned order
        if (order.getEmployee() != null) {
            throw new IllegalStateException("Order is already assigned.");
        }

        // Assign employee to order
        order.setEmployee(employee);

        // Update employee status
        employee.setStatus("Assigned");

        // Save changes
        orderRepository.save(order);
        employeeRepository.save(employee);

        return AssignOrderResponseDTO.builder()
                .employeeId(employee.getId())
                .employeeName(employee.getUser().getName())
                .orderId(order.getId())
                .message("Order assigned successfully")
                .build();
    }

    @Override
    public List<AssignedEmployeeDTO> getAssignedEmployees() {

        List<Order> orders = orderRepository.findByEmployeeIsNotNull();

        return orders.stream()
                .map(order -> AssignedEmployeeDTO.builder()
                        .employeeId(order.getEmployee().getId())
                        .employeeName(order.getEmployee().getUser().getName())
                        .orderId(order.getId())
                        .workingStatus(order.getWorkingStatus())
                        .build())
                .toList();
    }
}