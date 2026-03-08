package com.internalemployeeportal.domain.employee.application;

import com.internalemployeeportal.domain.auth.dto.request.SignUpReq;
import com.internalemployeeportal.domain.common.Status;
import com.internalemployeeportal.domain.employee.domain.Employee;
import com.internalemployeeportal.domain.employee.domain.EmployeeStatus;
import com.internalemployeeportal.domain.employee.domain.repository.EmployeeRepository;
import com.internalemployeeportal.domain.employee.dto.response.EmployeeListRes;
import com.internalemployeeportal.domain.employee.exception.EmployeeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        saveEmployee(newEmployee);
        generateEmployeeCode(newEmployee);
        return newEmployee;
    }

    @Transactional
    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Transactional
    public void terminateEmployee(Employee employee) {
        employee.updateStatus(Status.DISABLED);
        employee.updateEmployeeStatus(EmployeeStatus.TERMINATED);
        employeeRepository.save(employee);
    }

    public ResponseEntity<?> getEmployeeList() {
        List<Employee> employees = employeeRepository.findAll();
        List<EmployeeListRes> employeeListRes = employees.stream()
                .map(employee -> new EmployeeListRes(
                        employee.getEmployeeCode(),
                        employee.getFirstName() + " " + employee.getLastName(),
                        employee.getDepartment().getDepartmentName()))
                .toList();
        return ResponseEntity.ok(employeeListRes);
    }


    public Employee findByEmployeeId(Long employeeId) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        return optionalEmployee.orElseThrow(EmployeeNotFoundException::new);
    }

    @Transactional
    public void generateEmployeeCode(Employee employee) {
        // EMP-2024-001
        String employeeCode = String.format("EMP-%d-%03d", employee.getCreatedAt().getYear(), employee.getEmployeeId());
        employee.updateEmployeeCode(employeeCode);
        employeeRepository.save(employee);
    }

}
