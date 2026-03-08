package com.internalemployeeportal.domain.backgroundcheck.domain.repository;

import com.internalemployeeportal.domain.backgroundcheck.domain.BackgroundCheck;
import com.internalemployeeportal.domain.backgroundcheck.domain.CheckStatus;
import com.internalemployeeportal.domain.backgroundcheck.dto.response.BackgroundCheckPendingRes;
import com.internalemployeeportal.domain.employee.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BackgroundCheckRepository extends JpaRepository<BackgroundCheck, Long> {
    Optional<Object> findByEmployeeCode(String employeeCode);

    boolean existsByEmployee(Employee employee);

    BackgroundCheck findByEmployee(Employee employee);

    Optional<Object> findTopByEmployeeOrderByRequestedAtDesc(Employee employee);

    List<BackgroundCheck> findByEmployeeCodeAndCheckStatus(
            String employeeCode,
            CheckStatus checkStatus
    );

    List<BackgroundCheck> findByCheckStatus(CheckStatus checkStatus);
}
