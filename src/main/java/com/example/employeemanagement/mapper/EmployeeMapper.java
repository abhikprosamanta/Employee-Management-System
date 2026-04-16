package com.example.employeemanagement.mapper;

import com.example.employeemanagement.dto.employee.EmployeeCreateRequest;
import com.example.employeemanagement.dto.employee.EmployeeResponse;
import com.example.employeemanagement.dto.employee.EmployeeUpdateRequest;
import com.example.employeemanagement.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public Employee toEntity(EmployeeCreateRequest request) {
        return Employee.builder()
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .phoneNumber(trimToNull(request.getPhoneNumber()))
                .department(request.getDepartment().trim())
                .jobTitle(request.getJobTitle().trim())
                .salary(request.getSalary())
                .dateOfJoining(request.getDateOfJoining())
                .build();
    }

    public void updateEntity(Employee employee, EmployeeUpdateRequest request) {
        employee.setFirstName(request.getFirstName().trim());
        employee.setLastName(request.getLastName().trim());
        employee.setEmail(request.getEmail().trim().toLowerCase());
        employee.setPhoneNumber(trimToNull(request.getPhoneNumber()));
        employee.setDepartment(request.getDepartment().trim());
        employee.setJobTitle(request.getJobTitle().trim());
        employee.setSalary(request.getSalary());
        employee.setDateOfJoining(request.getDateOfJoining());
    }

    public EmployeeResponse toResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .phoneNumber(employee.getPhoneNumber())
                .department(employee.getDepartment())
                .jobTitle(employee.getJobTitle())
                .salary(employee.getSalary())
                .dateOfJoining(employee.getDateOfJoining())
                .createdByEmail(employee.getCreatedBy() == null ? null : employee.getCreatedBy().getEmail())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
