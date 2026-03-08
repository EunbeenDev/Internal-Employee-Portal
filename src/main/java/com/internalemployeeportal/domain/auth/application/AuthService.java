package com.internalemployeeportal.domain.auth.application;

import com.internalemployeeportal.domain.auth.domain.Token;
import com.internalemployeeportal.domain.auth.domain.repository.TokenRepository;
import com.internalemployeeportal.domain.auth.dto.request.LocalSignInReq;
import com.internalemployeeportal.domain.auth.dto.response.LoginResponse;
import com.internalemployeeportal.domain.auth.exception.AccountDisabledException;
import com.internalemployeeportal.domain.common.Status;
import com.internalemployeeportal.domain.employee.domain.EmployeeStatus;
import com.internalemployeeportal.domain.user.application.UserService;
import com.internalemployeeportal.domain.user.domain.Role;
import com.internalemployeeportal.domain.user.domain.User;
import com.internalemployeeportal.domain.user.domain.repository.UserRepository;
import com.internalemployeeportal.domain.user.exception.UserNotFoundException;
import com.internalemployeeportal.global.DefaultAssert;
import com.internalemployeeportal.global.exception.AuthenticationException;
import com.internalemployeeportal.global.config.security.token.UserPrincipal;
import com.internalemployeeportal.global.config.security.util.JwtTokenUtil;

import com.internalemployeeportal.global.payload.CommonApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;


    // accountId와 password를 받아서 로그인 처리
    @Transactional
    public ResponseEntity<?> localLogin(LocalSignInReq localSignInReq) {
        String accountId = localSignInReq.getAccountId();
        String password = localSignInReq.getPassword();

        User user = findUserByAccountId(accountId); // accountId로 사용자 조회
        validateUserPassword(password, user.getPasswordHash()); // 비밀번호 검증
        validateUserStatus(user); // 사용자 상태 검증
        validateEmployeeEmploymentStatus(user); // 직원의 고용 상태(퇴사 여부) 검증

        Map<String, Object> claims = new HashMap<>(); // JWT에 포함할 클레임 설정
        claims.put("userId", user.getUserId());
        claims.put("role", user.getRole().getValue());

        if (user.getEmployee() != null) {
            claims.put("employeeId", user.getEmployee().getEmployeeId());
        }

        // JWT 토큰 생성
        String accessToken = jwtTokenUtil.generateToken(claims, accountId);
        String refreshToken = jwtTokenUtil.generateRefreshToken(new HashMap<>(), accountId);

        saveOrUpdateRefreshToken(accountId, refreshToken);

        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        return ResponseEntity.ok(loginResponse);
    }


    @Transactional
    public ResponseEntity<?> logout(UserPrincipal userPrincipal) {

        // 사용자 검증
        User user = validateUser(userPrincipal);
        String accountId = user.getAccountId();

        // 토큰 정보 삭제
        deleteToken(accountId);

        return buildOkResponse("로그아웃이 완료되었습니다.");
    }

    // TODO: 직원 퇴사 시 로직


    @Transactional
    public ResponseEntity<?> signUp(String accountId, String password) {
        // 이미 존재하는 사용자 검증
        Optional<User> existingUser = userRepository.findByAccountId(accountId);
        DefaultAssert.isTrue(existingUser.isEmpty(), "이미 사용 중인 이메일입니다.");

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        // 사용자 생성
        User user = User.builder()
                .accountId(accountId)
                .passwordHash(encodedPassword)
                .role(Role.USER)
                .build();

        userRepository.save(user);

        return buildOkResponse("회원가입이 완료되었습니다.");
    }

    private void deleteToken(String accountId) {
        Token token = tokenRepository.findByAccountId(accountId);
        if (token != null) {
            tokenRepository.delete(token);
        }
    }

    // 사용자 검증 메서드
    private User validateUser(UserPrincipal userPrincipal) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(userPrincipal.getAccountId());
        if (userDetails instanceof UserPrincipal) {
            return userService.findByAccountId(((UserPrincipal) userDetails).getAccountId())
                    .orElseThrow(UserNotFoundException::new);
        } else {
            throw new UserNotFoundException();
        }
    }

    // accountId로 사용자 조회 메서드
    private User findUserByAccountId(String accountId) {
        return userRepository.findByAccountId(accountId)
                .orElseThrow(() -> new AuthenticationException("ID 또는 비밀번호가 올바르지 않습니다."));
    }

    // 비밀번호 검증 메서드
    private void validateUserPassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new AuthenticationException("accountId 또는 password가 올바르지 않습니다.");
        }
    }

    private void validateUserStatus(User user) {
        if (user.getStatus() != Status.ACTIVE) {
            throw new AccountDisabledException();
        }
    }

    private void validateEmployeeEmploymentStatus(User user) {
        if (user.getRole() == Role.USER &&
                user.getEmployee() != null &&
                user.getEmployee().getEmployeeStatus() == EmployeeStatus.TERMINATED) {
            throw new AccountDisabledException();
        }
    }

    private void saveOrUpdateRefreshToken(String accountId, String refreshToken) {
        Token existingToken = tokenRepository.findByAccountId(accountId);
        if (existingToken != null) {
            existingToken.updateRefreshToken(refreshToken);
            tokenRepository.save(existingToken);
        } else {
            Token newToken = Token.builder()
                    .accountId(accountId)
                    .refreshToken(refreshToken)
                    .build();
            tokenRepository.save(newToken);
        }
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
