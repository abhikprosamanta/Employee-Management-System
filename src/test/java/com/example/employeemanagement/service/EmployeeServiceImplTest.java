package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.employee.EmployeeCreateRequest;
import com.example.employeemanagement.dto.employee.EmployeeResponse;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.entity.Role;
import com.example.employeemanagement.entity.User;
import com.example.employeemanagement.exception.BadRequestException;
import com.example.employeemanagement.mapper.EmployeeMapper;
import com.example.employeemanagement.repository.EmployeeRepository;
import com.example.employeemanagement.repository.UserRepository;
import com.example.employeemanagement.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Test
    void createEmployeeSavesEmployeeAndReturnsResponse() {
        EmployeeCreateRequest request = EmployeeCreateRequest.builder()
                .firstName("Ada")
                .lastName("Lovelace")
                .email("ada@example.com")
                .phoneNumber("+1 555 0100")
                .department("Engineering")
                .jobTitle("Principal Engineer")
                .salary(new BigDecimal("150000.00"))
                .dateOfJoining(LocalDate.of(2024, 1, 15))
                .build();

        User creator = User.builder()
                .id(1L)
                .name("Admin")
                .email("admin@example.com")
                .role(Role.ADMIN)
                .build();

        Employee employee = Employee.builder()
                .firstName("Ada")
                .lastName("Lovelace")
                .email("ada@example.com")
                .department("Engineering")
                .jobTitle("Principal Engineer")
                .salary(new BigDecimal("150000.00"))
                .dateOfJoining(LocalDate.of(2024, 1, 15))
                .build();

        Employee savedEmployee = Employee.builder()
                .id(10L)
                .firstName("Ada")
                .lastName("Lovelace")
                .email("ada@example.com")
                .department("Engineering")
                .jobTitle("Principal Engineer")
                .salary(new BigDecimal("150000.00"))
                .dateOfJoining(LocalDate.of(2024, 1, 15))
                .createdBy(creator)
                .build();

        EmployeeResponse response = EmployeeResponse.builder()
                .id(10L)
                .firstName("Ada")
                .lastName("Lovelace")
                .email("ada@example.com")
                .department("Engineering")
                .jobTitle("Principal Engineer")
                .salary(new BigDecimal("150000.00"))
                .dateOfJoining(LocalDate.of(2024, 1, 15))
                .createdByEmail("admin@example.com")
                .build();

        when(employeeRepository.existsByEmailIgnoreCase("ada@example.com")).thenReturn(false);
        when(employeeMapper.toEntity(request)).thenReturn(employee);
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(creator));
        when(employeeRepository.save(employee)).thenReturn(savedEmployee);
        when(employeeMapper.toResponse(savedEmployee)).thenReturn(response);

        EmployeeResponse result = employeeService.createEmployee(request, "admin@example.com");

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getEmail()).isEqualTo("ada@example.com");
        assertThat(employee.getCreatedBy()).isEqualTo(creator);
        verify(employeeRepository).save(employee);
    }

    @Test
    void createEmployeeThrowsWhenEmailAlreadyExists() {
        EmployeeCreateRequest request = EmployeeCreateRequest.builder()
                .firstName("Ada")
                .lastName("Lovelace")
                .email("ada@example.com")
                .department("Engineering")
                .jobTitle("Principal Engineer")
                .salary(new BigDecimal("150000.00"))
                .dateOfJoining(LocalDate.of(2024, 1, 15))
                .build();

        when(employeeRepository.existsByEmailIgnoreCase("ada@example.com")).thenReturn(true);

        assertThatThrownBy(() -> employeeService.createEmployee(request, "admin@example.com"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already exists");
    }
}
