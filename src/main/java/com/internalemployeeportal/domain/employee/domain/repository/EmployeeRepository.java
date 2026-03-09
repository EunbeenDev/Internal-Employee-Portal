package com.internalemployeeportal.domain.employee.domain.repository;

import com.internalemployeeportal.domain.employee.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Object> findByEmployeeCode(String employeeCode);
}
