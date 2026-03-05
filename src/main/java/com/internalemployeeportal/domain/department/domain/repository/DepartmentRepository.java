package com.internalemployeeportal.domain.department.domain.repository;

import com.internalemployeeportal.domain.department.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
