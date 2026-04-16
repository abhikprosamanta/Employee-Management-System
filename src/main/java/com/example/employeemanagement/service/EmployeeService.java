package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.common.PageResponse;
import com.example.employeemanagement.dto.employee.EmployeeCreateRequest;
import com.example.employeemanagement.dto.employee.EmployeeResponse;
import com.example.employeemanagement.dto.employee.EmployeeUpdateRequest;

public interface EmployeeService {

    EmployeeResponse createEmployee(EmployeeCreateRequest request, String createdByEmail);

    PageResponse<EmployeeResponse> getEmployees(int page, int size, String sortBy, String sortDir, String name, String department);

    EmployeeResponse getEmployeeById(Long id);

    EmployeeResponse updateEmployee(Long id, EmployeeUpdateRequest request);

    void deleteEmployee(Long id);
}
