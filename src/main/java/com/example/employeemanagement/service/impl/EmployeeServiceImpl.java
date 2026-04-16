package com.example.employeemanagement.service.impl;

import com.example.employeemanagement.dto.common.PageResponse;
import com.example.employeemanagement.dto.employee.EmployeeCreateRequest;
import com.example.employeemanagement.dto.employee.EmployeeResponse;
import com.example.employeemanagement.dto.employee.EmployeeUpdateRequest;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.exception.BadRequestException;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.mapper.EmployeeMapper;
import com.example.employeemanagement.repository.EmployeeRepository;
import com.example.employeemanagement.repository.UserRepository;
import com.example.employeemanagement.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id",
            "firstName",
            "lastName",
            "email",
            "department",
            "jobTitle",
            "dateOfJoining",
            "createdAt",
            "updatedAt"
    );

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    @Transactional
    public EmployeeResponse createEmployee(EmployeeCreateRequest request, String createdByEmail) {
        String email = normalizeEmail(request.getEmail());
        if (employeeRepository.existsByEmailIgnoreCase(email)) {
            throw new BadRequestException("An employee already exists with email: " + email);
        }

        Employee employee = employeeMapper.toEntity(request);
        userRepository.findByEmail(normalizeEmail(createdByEmail)).ifPresent(employee::setCreatedBy);

        Employee savedEmployee = employeeRepository.save(employee);
        log.info("Employee created with id: {}", savedEmployee.getId());
        return employeeMapper.toResponse(savedEmployee);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EmployeeResponse> getEmployees(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String name,
            String department) {
        Pageable pageable = buildPageable(page, size, sortBy, sortDir);
        Page<EmployeeResponse> employeePage = employeeRepository
                .searchEmployees(blankToNull(name), blankToNull(department), pageable)
                .map(employeeMapper::toResponse);

        return PageResponse.from(employeePage);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = findEmployeeOrThrow(id);
        return employeeMapper.toResponse(employee);
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeUpdateRequest request) {
        Employee employee = findEmployeeOrThrow(id);
        String requestedEmail = normalizeEmail(request.getEmail());

        if (!employee.getEmail().equalsIgnoreCase(requestedEmail)
                && employeeRepository.existsByEmailIgnoreCase(requestedEmail)) {
            throw new BadRequestException("An employee already exists with email: " + requestedEmail);
        }

        employeeMapper.updateEntity(employee, request);
        Employee savedEmployee = employeeRepository.save(employee);
        log.info("Employee updated with id: {}", savedEmployee.getId());
        return employeeMapper.toResponse(savedEmployee);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = findEmployeeOrThrow(id);
        employeeRepository.delete(employee);
        log.info("Employee deleted with id: {}", id);
    }

    private Employee findEmployeeOrThrow(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    private Pageable buildPageable(int page, int size, String sortBy, String sortDir) {
        if (page < 0) {
            throw new BadRequestException("Page index must not be negative.");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new BadRequestException("Page size must be between 1 and " + MAX_PAGE_SIZE + ".");
        }
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new BadRequestException("Unsupported sort field: " + sortBy);
        }

        Sort.Direction direction = parseSortDirection(sortDir);
        return PageRequest.of(page, size, Sort.by(direction, sortBy));
    }

    private Sort.Direction parseSortDirection(String sortDir) {
        if ("asc".equalsIgnoreCase(sortDir)) {
            return Sort.Direction.ASC;
        }
        if ("desc".equalsIgnoreCase(sortDir)) {
            return Sort.Direction.DESC;
        }
        throw new BadRequestException("Sort direction must be either asc or desc.");
    }

    private String blankToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
