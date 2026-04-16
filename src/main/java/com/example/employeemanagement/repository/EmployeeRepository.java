package com.example.employeemanagement.repository;

import com.example.employeemanagement.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmailIgnoreCase(String email);

    @Query("""
            SELECT e FROM Employee e
            WHERE (:name IS NULL OR
                   LOWER(e.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR
                   LOWER(e.lastName) LIKE LOWER(CONCAT('%', :name, '%')))
              AND (:department IS NULL OR LOWER(e.department) LIKE LOWER(CONCAT('%', :department, '%')))
            """)
    Page<Employee> searchEmployees(
            @Param("name") String name,
            @Param("department") String department,
            Pageable pageable);
}
