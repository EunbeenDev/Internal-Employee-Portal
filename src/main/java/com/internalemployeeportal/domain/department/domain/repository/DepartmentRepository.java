package com.internalemployeeportal.domain.department.domain.repository;

import com.internalemployeeportal.domain.department.domain.Department;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Object> findByDepartmentName(@NotBlank(message = "부서는 필수입니다.") String department);
}
