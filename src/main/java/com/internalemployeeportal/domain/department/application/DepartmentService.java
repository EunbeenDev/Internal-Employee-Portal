package com.internalemployeeportal.domain.department.application;

import com.internalemployeeportal.domain.department.domain.Department;
import com.internalemployeeportal.domain.department.domain.repository.DepartmentRepository;
import com.internalemployeeportal.domain.employee.domain.Employee;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    public void assignDepartmentToEmployee(Employee newEmployee, @NotBlank(message = "부서는 필수입니다.") String department) {
        // 부서 이름이 존재하는지 확인
        departmentRepository.findByDepartmentName(department)
                .ifPresentOrElse(
                        existingDepartment -> {
                            // 기존 부서가 존재하면 직원에 할당
                            newEmployee.updateDepartment((Department) existingDepartment);
                        },
                        () -> {
                            // 기존 부서가 존재하지 않으면 새로 생성하여 직원에 할당
                            Department newDepartment = Department.builder()
                                    .departmentName(department)
                                    .build();
                            departmentRepository.save(newDepartment);
                            newEmployee.updateDepartment(newDepartment);
                        }
                );
    }
}
