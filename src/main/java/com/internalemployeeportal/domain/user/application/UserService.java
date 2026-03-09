package com.internalemployeeportal.domain.user.application;


import com.internalemployeeportal.domain.auth.dto.request.SignUpReq;
import com.internalemployeeportal.domain.auth.exception.AccountDisabledException;
import com.internalemployeeportal.domain.common.Status;
import com.internalemployeeportal.domain.employee.application.EmployeeService;
import com.internalemployeeportal.domain.employee.domain.Employee;
import com.internalemployeeportal.domain.employee.domain.EmployeeStatus;
import com.internalemployeeportal.domain.user.domain.Role;
import com.internalemployeeportal.domain.user.domain.User;
import com.internalemployeeportal.domain.user.domain.repository.UserRepository;
import com.internalemployeeportal.domain.user.exception.AccountIdAlreadyExistsException;
import com.internalemployeeportal.domain.user.exception.UserNotFoundException;
import com.internalemployeeportal.global.DefaultAssert;
import com.internalemployeeportal.global.config.security.token.UserPrincipal;
import com.internalemployeeportal.global.payload.CommonApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final EmployeeService employeeService;
    private final PasswordEncoder passwordEncoder;


    // 새로운 계정 생성
    @Transactional
    public ResponseEntity<?> createAccount(SignUpReq signUpReq) {
        // 계정 ID 중복 확인
        validUniqueAccountId(signUpReq.getAccountId());
        String encodedPassword = passwordEncoder.encode(signUpReq.getPassword());

        // 직원 객체 생성(EmployeeService에서 생성 로직을 분리하여 구현)
        Employee newEmployee = employeeService.createEmployeeForNewAccount(signUpReq);

        // 새로운 User 엔티티 생성
        User newUser = User.builder()
                .accountId(signUpReq.getAccountId())
                .passwordHash(encodedPassword)
                .role(Role.USER)
                .employee(newEmployee)
                .build();
        // User 엔티티 저장
        saveUser(newUser);
        return buildOkResponse("계정이 성공적으로 생성되었습니다.");
    }

    private void validUniqueAccountId(String accountId) {
        if (userRepository.existsByAccountId(accountId)) {
            throw new AccountIdAlreadyExistsException();
        }
    }

    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }

    public ResponseEntity<?> terminateEmployee(Long employeeId) {
        // 직원 조회
        Employee employee = employeeService.findByEmployeeId(employeeId);
        // 직원 상태를 TERMINATED로 변경
        employeeService.terminateEmployee(employee);
        return ResponseEntity.ok("직원이 성공적으로 퇴사 처리되었습니다.");
    }

    public Optional<User> findByAccountId(String accountId) {
        return userRepository.findByAccountId(accountId);
    }

    // 계정 ID로 사용자 조회 및 활성 상태 확인
    public void findActiveUser(String accountId) {

        User user = userRepository.findByAccountId(accountId)
                .orElseThrow(UserNotFoundException::new);
        // 사용자 상태가 ACTIVE인지 확인
        if (user.getStatus() != Status.ACTIVE) {
            throw new AccountDisabledException();
        }
        // 퇴사 여부 확인
        if (user.getEmployee() != null &&
                user.getEmployee().getEmployeeStatus() == EmployeeStatus.TERMINATED) {
            throw new AccountDisabledException();
        }

    }

    private User validUserByUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        DefaultAssert.isTrue(user.isPresent(), "사용자가 존재하지 않습니다.");
        return user.get();
    }

    public User findUserByUserId(long userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    // 응답 빌더 메서드
    private ResponseEntity<?> buildOkResponse(String message) {
        CommonApiResponse<Object> response = CommonApiResponse.builder()
                .check(true)
                .information(message)
                .build();
        return ResponseEntity.ok(response);
    }



}
