package com.internalemployeeportal.domain.user.application;


import com.internalemployeeportal.domain.auth.exception.AccountDisabledException;
import com.internalemployeeportal.domain.common.Status;
import com.internalemployeeportal.domain.employee.domain.EmployeeStatus;
import com.internalemployeeportal.domain.user.domain.User;
import com.internalemployeeportal.domain.user.domain.repository.UserRepository;
import com.internalemployeeportal.domain.user.exception.UserNotFoundException;
import com.internalemployeeportal.global.DefaultAssert;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    @Transactional
    public void saveUser(User user) {
        // 유저 정보 저장
        userRepository.save(user);
    }

    public Optional<User> findByAccountId(String accountId) {
        return userRepository.findByAccountId(accountId);
    }

    public User findActiveUser(String accountId) {

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

        return user;
    }

    private User validUserByUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        DefaultAssert.isTrue(user.isPresent(), "사용자가 존재하지 않습니다.");
        return user.get();
    }

    public User findUserByUserId(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    // 사용자 존재 여부
    public boolean existsByAccountId(String accountId) {
        return userRepository.existsByAccountId(accountId);
    }
}
