package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.admin.AdminUserRequest;
import com.redshanflora.redshanflora_backend.dto.admin.AdminUserResponse;
import com.redshanflora.redshanflora_backend.entity.Customer;
import com.redshanflora.redshanflora_backend.entity.Employee;
import com.redshanflora.redshanflora_backend.entity.Manager;
import com.redshanflora.redshanflora_backend.entity.User;
import com.redshanflora.redshanflora_backend.enums.Role;
import com.redshanflora.redshanflora_backend.exception.EmailAlreadyExistsException;
import com.redshanflora.redshanflora_backend.repository.CustomerRepository;
import com.redshanflora.redshanflora_backend.repository.EmployeeRepository;
import com.redshanflora.redshanflora_backend.repository.ManagerRepository;
import com.redshanflora.redshanflora_backend.repository.UserRepository;
import com.redshanflora.redshanflora_backend.service.impl.AdminUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdminUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ManagerRepository managerRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminUserServiceImpl adminUserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterManager_Success() {
        // Arrange
        AdminUserRequest request = AdminUserRequest.builder()
                .name("Manager Name")
                .email("manager@example.com")
                .password("password123")
                .phone("1234567890")
                .build();

        User savedUser = User.builder()
                .id(10L)
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password("encodedPassword123")
                .role(Role.MANAGER)
                .registeredDate(Instant.now())
                .build();

        Manager savedManager = Manager.builder()
                .id(5L)
                .user(savedUser)
                .promoteDate(Instant.now())
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(managerRepository.save(any(Manager.class))).thenReturn(savedManager);
        when(managerRepository.findByUser_Id(savedUser.getId())).thenReturn(java.util.Optional.of(savedManager));

        // Act
        AdminUserResponse response = adminUserService.registerManager(request);

        // Assert
        assertNotNull(response);
        assertEquals(savedUser.getId(), response.getId());
        assertEquals(savedUser.getName(), response.getName());
        assertEquals(savedUser.getEmail(), response.getEmail());
        assertEquals(savedUser.getPhone(), response.getPhone());
        assertEquals("MANAGER", response.getRole());
        assertEquals(savedUser.getRegisteredDate(), response.getRegisteredDate());
        assertEquals(savedManager.getId(), response.getManagerId());
        assertEquals(savedManager.getPromoteDate(), response.getPromoteDate());

        verify(userRepository, times(1)).existsByEmail(request.getEmail());
        verify(passwordEncoder, times(1)).encode(request.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
        verify(managerRepository, times(1)).save(any(Manager.class));
    }

    @Test
    void testRegisterManager_EmailAlreadyExists() {
        // Arrange
        AdminUserRequest request = AdminUserRequest.builder()
                .name("Manager Name")
                .email("manager@example.com")
                .password("password123")
                .phone("1234567890")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // Act & Assert
        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class, () -> {
            adminUserService.registerManager(request);
        });

        assertEquals("Email already in use", exception.getMessage());

        verify(userRepository, times(1)).existsByEmail(request.getEmail());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
        verify(managerRepository, never()).save(any());
    }

    @Test
    void testRegisterEmployee_Success() {
        // Arrange
        AdminUserRequest request = AdminUserRequest.builder()
                .name("Employee Name")
                .email("employee@example.com")
                .password("password123")
                .phone("0987654321")
                .build();

        User savedUser = User.builder()
                .id(20L)
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password("encodedPassword123")
                .role(Role.EMPLOYEE)
                .registeredDate(Instant.now())
                .build();

        Employee savedEmployee = Employee.builder()
                .id(15L)
                .user(savedUser)
                .promoteDate(Instant.now())
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);
        when(employeeRepository.findByUser_Id(savedUser.getId())).thenReturn(java.util.Optional.of(savedEmployee));

        // Act
        AdminUserResponse response = adminUserService.registerEmployee(request);

        // Assert
        assertNotNull(response);
        assertEquals(savedUser.getId(), response.getId());
        assertEquals(savedUser.getName(), response.getName());
        assertEquals(savedUser.getEmail(), response.getEmail());
        assertEquals(savedUser.getPhone(), response.getPhone());
        assertEquals("EMPLOYEE", response.getRole());
        assertEquals(savedUser.getRegisteredDate(), response.getRegisteredDate());
        assertEquals(savedEmployee.getId(), response.getEmployeeId());
        assertEquals(savedEmployee.getPromoteDate(), response.getPromoteDate());

        verify(userRepository, times(1)).existsByEmail(request.getEmail());
        verify(passwordEncoder, times(1)).encode(request.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void testRegisterEmployee_EmailAlreadyExists() {
        // Arrange
        AdminUserRequest request = AdminUserRequest.builder()
                .name("Employee Name")
                .email("employee@example.com")
                .password("password123")
                .phone("0987654321")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // Act & Assert
        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class, () -> {
            adminUserService.registerEmployee(request);
        });

        assertEquals("Email already in use", exception.getMessage());

        verify(userRepository, times(1)).existsByEmail(request.getEmail());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void testSearchUsers() {
        // Arrange
        User user1 = User.builder().id(1L).name("Kasun Fernando").email("kasun@example.com").role(Role.MANAGER).build();
        User user2 = User.builder().id(2L).name("Jane Doe").email("jane@example.com").role(Role.EMPLOYEE).build();
        when(userRepository.searchUsersByRole(Role.MANAGER, "Kasun")).thenReturn(java.util.Arrays.asList(user1, user2));

        // Act
        java.util.List<AdminUserResponse> responses = adminUserService.getUsers("MANAGER", "Kasun");

        // Assert
        assertEquals(2, responses.size());
        verify(userRepository, times(1)).searchUsersByRole(Role.MANAGER, "Kasun");
    }

    @Test
    void testGetUsersByRoleWithoutSearch() {
        // Arrange
        User user = User.builder().id(3L).name("Customer Name").email("customer@example.com").role(Role.CUSTOMER).build();
        when(userRepository.findByRole(Role.CUSTOMER)).thenReturn(java.util.List.of(user));

        // Act
        java.util.List<AdminUserResponse> responses = adminUserService.getUsers("CUSTOMER", "");

        // Assert
        assertEquals(1, responses.size());
        assertEquals("CUSTOMER", responses.get(0).getRole());
        verify(userRepository, times(1)).findByRole(Role.CUSTOMER);
    }

    @Test
    void testUpdateUser() {
        // Arrange
        User existingUser = User.builder()
                .id(1L)
                .name("Old Name")
                .email("old@example.com")
                .phone("123")
                .role(Role.MANAGER)
                .build();

        AdminUserRequest request = AdminUserRequest.builder()
                .name("New Name")
                .email("new@example.com")
                .phone("456")
                .role("EMPLOYEE")
                .build();

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(existingUser));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        Manager existingManager = Manager.builder().id(10L).user(existingUser).build();
        when(managerRepository.findByUser_Id(1L)).thenReturn(java.util.Optional.of(existingManager));

        Employee savedEmployee = Employee.builder().id(20L).user(existingUser).build();
        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);

        // Act
        AdminUserResponse response = adminUserService.updateUser(1L, request);

        // Assert
        assertNotNull(response);
        assertEquals("New Name", existingUser.getName());
        assertEquals("new@example.com", existingUser.getEmail());
        assertEquals("456", existingUser.getPhone());
        assertEquals(Role.EMPLOYEE, existingUser.getRole());

        verify(managerRepository, times(1)).delete(existingManager);
        verify(employeeRepository, times(1)).save(any(Employee.class));
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void testDeleteUser() {
        // Arrange
        User existingUser = User.builder().id(1L).name("User").email("user@example.com").build();
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(existingUser));

        Manager existingManager = Manager.builder().id(10L).user(existingUser).build();
        when(managerRepository.findByUser_Id(1L)).thenReturn(java.util.Optional.of(existingManager));

        // Act
        adminUserService.deleteUser(1L);

        // Assert
        verify(managerRepository, times(1)).delete(existingManager);
        verify(userRepository, times(1)).delete(existingUser);
    }
}
