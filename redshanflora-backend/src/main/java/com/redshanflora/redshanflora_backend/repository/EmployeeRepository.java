package com.redshanflora.redshanflora_backend.repository;


import com.redshanflora.redshanflora_backend.entity.Employee;
import com.redshanflora.redshanflora_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

    @Repository
    public interface EmployeeRepository extends JpaRepository<Employee, Long> {
        Optional<Employee> findByUser(User user);
        Optional<Employee> findByUser_Id(Long userId);
        List<Employee> findByStatus(String status);

    }

