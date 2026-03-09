package com.internalemployeeportal.domain.employee.application;

import com.internalemployeeportal.domain.auth.dto.request.SignUpReq;
import com.internalemployeeportal.domain.common.Status;
import com.internalemployeeportal.domain.department.application.DepartmentService;
import com.internalemployeeportal.domain.employee.domain.Employee;
import com.internalemployeeportal.domain.employee.domain.EmployeeStatus;
import com.internalemployeeportal.domain.employee.domain.repository.EmployeeRepository;
import com.internalemployeeportal.domain.employee.dto.request.MyInfoUpdateReq;
import com.internalemployeeportal.domain.employee.dto.response.EmployeeListRes;
import com.internalemployeeportal.domain.employee.dto.response.MyInfoRes;
import com.internalemployeeportal.domain.employee.exception.EmployeeNotFoundException;
import com.internalemployeeportal.domain.user.application.UserService;
import com.internalemployeeportal.domain.user.domain.User;
import com.internalemployeeportal.domain.user.domain.repository.UserRepository;
import com.internalemployeeportal.domain.user.exception.UserNotFoundException;
import com.internalemployeeportal.global.config.security.token.UserPrincipal;
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
    private final DepartmentService departmentService;
    private final UserRepository userRepository;

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
        // 부서 할당(DepartmentService)
        departmentService.assignDepartmentToEmployee(newEmployee, signUpReq.getDepartment());
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
                        employee.getDepartment().getDepartmentName(),
                        employee.getEmployeeStatus(),
                        employee.getEmployeeStatus() == EmployeeStatus.TERMINATED))
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

    public ResponseEntity<?> getMyInfo(UserPrincipal userPrincipal) {
        User user = userRepository.findByAccountId(userPrincipal.getAccountId())
                .orElseThrow(UserNotFoundException::new);

        // 현재 로그인한 사용자의 Employee 정보 조회
        Employee employee = user.getEmployee();

        // Employee 정보를 MyInfoRes DTO로 변환하여 반환
        MyInfoRes myInfoRes = MyInfoRes.builder().
                employeeCode(employee.getEmployeeCode())
                .name(employee.getFirstName() + " " + employee.getLastName())
                .email(employee.getEmail())
                .dateOfBirth(employee.getDateOfBirth())
                .departmentName(employee.getDepartment().getDepartmentName())
                .build();

        return ResponseEntity.ok(myInfoRes);
    }

    @Transactional
    public ResponseEntity<?> updateMyInfo(UserPrincipal userPrincipal, MyInfoUpdateReq myInfoUpdateReq) {
        User user = userRepository.findByAccountId(userPrincipal.getAccountId())
                .orElseThrow(UserNotFoundException::new);

        // 현재 로그인한 사용자의 Employee 정보 조회
        Employee employee = user.getEmployee();

        // Employee 정보 업데이트
        employee.updateEmployeeInfo(myInfoUpdateReq.getFirstName(), myInfoUpdateReq.getLastName(), myInfoUpdateReq.getEmail(), myInfoUpdateReq.getDateOfBirth());

        // 변경된 Employee 정보 저장
        employeeRepository.save(employee);

        return ResponseEntity.ok("내 정보가 성공적으로 업데이트되었습니다.");
    }

    public Optional<Object> findByEmployeeCode(String employeeCode) {
        return employeeRepository.findByEmployeeCode(employeeCode);
    }
}
