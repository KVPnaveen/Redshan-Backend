package com.redshanflora.redshanflora_backend.service.impl;

import com.redshanflora.redshanflora_backend.dto.admin.AdminUserRequest;
import com.redshanflora.redshanflora_backend.dto.admin.AdminUserResponse;
import com.redshanflora.redshanflora_backend.dto.admin.AdminUserStatsResponse;
import com.redshanflora.redshanflora_backend.entity.Customer;
import com.redshanflora.redshanflora_backend.entity.Employee;
import com.redshanflora.redshanflora_backend.entity.Manager;
import com.redshanflora.redshanflora_backend.entity.User;
import com.redshanflora.redshanflora_backend.enums.Role;
import com.redshanflora.redshanflora_backend.exception.EmailAlreadyExistsException;
import com.redshanflora.redshanflora_backend.exception.ResourceNotFoundException;
import com.redshanflora.redshanflora_backend.repository.CustomerRepository;
import com.redshanflora.redshanflora_backend.repository.EmployeeRepository;
import com.redshanflora.redshanflora_backend.repository.ManagerRepository;
import com.redshanflora.redshanflora_backend.repository.UserRepository;
import com.redshanflora.redshanflora_backend.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final ManagerRepository managerRepository;
    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AdminUserResponse registerManager(AdminUserRequest request) {
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required for registration");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already in use");
        }

        // 1. Build and save the main User entity with Role MANAGER
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.MANAGER)
                .status(request.getStatus() != null ? request.getStatus().toUpperCase() : "ACTIVE")
                .build();

        if (request.getRegisteredDate() != null) {
            user.setRegisteredDate(request.getRegisteredDate());
        }

        User savedUser = userRepository.save(user);

        // 2. Build and save the associated Manager entity
        Manager manager = Manager.builder()
                .user(savedUser)
                .promoteDate(request.getPromoteDate() != null ? request.getPromoteDate() : Instant.now())
                .build();

        Manager savedManager = managerRepository.save(manager);

        // 3. Return the response DTO
        return mapToAdminUserResponse(savedUser);
    }

    @Override
    @Transactional
    public AdminUserResponse registerEmployee(AdminUserRequest request) {
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required for registration");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already in use");
        }

        // 1. Build and save the main User entity with Role EMPLOYEE
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.EMPLOYEE)
                .status(request.getStatus() != null ? request.getStatus().toUpperCase() : "ACTIVE")
                .build();

        if (request.getRegisteredDate() != null) {
            user.setRegisteredDate(request.getRegisteredDate());
        }

        User savedUser = userRepository.save(user);

        // 2. Build and save the associated Employee entity
        Employee employee = Employee.builder()
                .user(savedUser)
                .promoteDate(request.getPromoteDate() != null ? request.getPromoteDate() : Instant.now())
                .build();

        Employee savedEmployee = employeeRepository.save(employee);

        // 3. Return the response DTO
        return mapToAdminUserResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminUserResponse> getUsers(String role, String search) {
        Role parsedRole = null;
        if (role != null && !role.trim().isEmpty()) {
            parsedRole = Role.valueOf(role.toUpperCase());
        }

        String searchPattern = null;
        if (search != null && !search.trim().isEmpty()) {
            searchPattern = search.trim();
        }

        List<User> users;
        if (parsedRole != null && searchPattern != null) {
            users = userRepository.searchUsersByRole(parsedRole, searchPattern);
        } else if (parsedRole != null) {
            users = userRepository.findByRole(parsedRole);
        } else if (searchPattern != null) {
            users = userRepository.searchUsers(searchPattern);
        } else {
            users = userRepository.findAll();
        }

        return users.stream()
                .map(this::mapToAdminUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToAdminUserResponse(user);
    }

    @Override
    @Transactional
    public AdminUserResponse updateUser(Long id, AdminUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Email uniqueness check
        if (!user.getEmail().equalsIgnoreCase(request.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new EmailAlreadyExistsException("Email already in use");
            }
            user.setEmail(request.getEmail());
        }

        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setStatus(request.getStatus() != null ? request.getStatus().toUpperCase() : "ACTIVE");

        if (request.getRegisteredDate() != null) {
            user.setRegisteredDate(request.getRegisteredDate());
        }

        // Password update check (optional)
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Role update logic (optional)
        if (request.getRole() != null && !request.getRole().trim().isEmpty()) {
            Role newRole = Role.valueOf(request.getRole().toUpperCase());
            Role oldRole = user.getRole();
            if (oldRole != newRole) {
                // Delete old profile
                if (oldRole == Role.CUSTOMER) {
                    customerRepository.findByUser_Id(user.getId()).ifPresent(customerRepository::delete);
                } else if (oldRole == Role.MANAGER) {
                    managerRepository.findByUser_Id(user.getId()).ifPresent(managerRepository::delete);
                } else if (oldRole == Role.EMPLOYEE) {
                    employeeRepository.findByUser_Id(user.getId()).ifPresent(employeeRepository::delete);
                }

                // Create new profile
                if (newRole == Role.CUSTOMER) {
                    Customer customer = Customer.builder()
                            .user(user)
                            .promoteDate(request.getPromoteDate() != null ? request.getPromoteDate() : Instant.now())
                            .build();
                    customerRepository.save(customer);
                } else if (newRole == Role.MANAGER) {
                    Manager manager = Manager.builder()
                            .user(user)
                            .promoteDate(request.getPromoteDate() != null ? request.getPromoteDate() : Instant.now())
                            .build();
                    managerRepository.save(manager);
                } else if (newRole == Role.EMPLOYEE) {
                    Employee employee = Employee.builder()
                            .user(user)
                            .promoteDate(request.getPromoteDate() != null ? request.getPromoteDate() : Instant.now())
                            .build();
                    employeeRepository.save(employee);
                }
                user.setRole(newRole);
            } else {
                // Update promote date on the existing role entity if it did not change
                if (user.getRole() == Role.MANAGER) {
                    managerRepository.findByUser_Id(user.getId()).ifPresent(m -> {
                        if (request.getPromoteDate() != null) {
                            m.setPromoteDate(request.getPromoteDate());
                            managerRepository.save(m);
                        }
                    });
                } else if (user.getRole() == Role.EMPLOYEE) {
                    employeeRepository.findByUser_Id(user.getId()).ifPresent(e -> {
                        if (request.getPromoteDate() != null) {
                            e.setPromoteDate(request.getPromoteDate());
                            employeeRepository.save(e);
                        }
                    });
                } else if (user.getRole() == Role.CUSTOMER) {
                    customerRepository.findByUser_Id(user.getId()).ifPresent(c -> {
                        if (request.getPromoteDate() != null) {
                            c.setPromoteDate(request.getPromoteDate());
                            customerRepository.save(c);
                        }
                    });
                }
            }
        }

        User updatedUser = userRepository.save(user);
        return mapToAdminUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Manually delete profiles if they exist to prevent foreign key violations
        customerRepository.findByUser_Id(id).ifPresent(customerRepository::delete);
        managerRepository.findByUser_Id(id).ifPresent(managerRepository::delete);
        employeeRepository.findByUser_Id(id).ifPresent(employeeRepository::delete);

        userRepository.delete(user);
    }

    private AdminUserResponse mapToAdminUserResponse(User user) {
        AdminUserResponse.AdminUserResponseBuilder builder = AdminUserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .status(user.getStatus())
                .registeredDate(user.getRegisteredDate());

        if (user.getRole() == Role.CUSTOMER) {
            customerRepository.findByUser_Id(user.getId()).ifPresent(c -> {
                builder.customerId(c.getId());
                builder.promoteDate(c.getPromoteDate());
            });
        } else if (user.getRole() == Role.MANAGER) {
            managerRepository.findByUser_Id(user.getId()).ifPresent(m -> {
                builder.managerId(m.getId());
                builder.promoteDate(m.getPromoteDate());
            });
        } else if (user.getRole() == Role.EMPLOYEE) {
            employeeRepository.findByUser_Id(user.getId()).ifPresent(e -> {
                builder.employeeId(e.getId());
                builder.promoteDate(e.getPromoteDate());
            });
        }

        return builder.build();

    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserStatsResponse getUserStats() {
        long totalCustomers = userRepository.countByRole(Role.CUSTOMER);
        long totalManagers = userRepository.countByRole(Role.MANAGER);
        long totalEmployees = userRepository.countByRole(Role.EMPLOYEE);

        // Get start and end of this month and last month
        java.time.ZonedDateTime now = java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC);
        
        java.time.ZonedDateTime startOfThisMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        java.time.ZonedDateTime startOfLastMonth = startOfThisMonth.minusMonths(1);

        Instant thisMonthStartInstant = startOfThisMonth.toInstant();
        Instant lastMonthStartInstant = startOfLastMonth.toInstant();
        Instant nowInstant = now.toInstant();

        long customersThisMonth = userRepository.countByRoleAndRegisteredDateBetween(Role.CUSTOMER, thisMonthStartInstant, nowInstant);
        long customersLastMonth = userRepository.countByRoleAndRegisteredDateBetween(Role.CUSTOMER, lastMonthStartInstant, thisMonthStartInstant);

        double percentageIncrease = 0.0;
        if (customersLastMonth > 0) {
            percentageIncrease = ((double) (customersThisMonth - customersLastMonth) / customersLastMonth) * 100.0;
        } else if (customersThisMonth > 0) {
            percentageIncrease = 100.0;
        }

        return AdminUserStatsResponse.builder()
                .totalCustomers(totalCustomers)
                .customerIncreasePercentage(percentageIncrease)
                .totalManagers(totalManagers)
                .totalEmployees(totalEmployees)
                .build();

    }
}
