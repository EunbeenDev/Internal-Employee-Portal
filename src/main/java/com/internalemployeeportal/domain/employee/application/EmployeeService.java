package com.internalemployeeportal.domain.employee.application;

import com.internalemployeeportal.domain.auth.dto.request.SignUpReq;
import com.internalemployeeportal.domain.employee.domain.Employee;
import com.internalemployeeportal.domain.employee.domain.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Transactional
    public Employee createEmployeeForNewAccount(SignUpReq signUpReq) {
        Employee newEmployee = Employee.builder()
                .firstName(signUpReq.getFirstName())
                .lastName(signUpReq.getLastName())
                .email(signUpReq.getEmail())
                .dateOfBirth(signUpReq.getDateOfBirth())
                .build();
        return saveEmployee(newEmployee);
    }

    @Transactional
    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }
}
