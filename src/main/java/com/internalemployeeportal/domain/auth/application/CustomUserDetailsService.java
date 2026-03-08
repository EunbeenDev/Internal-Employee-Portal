package com.internalemployeeportal.domain.auth.application;

import com.internalemployeeportal.domain.user.domain.User;
import com.internalemployeeportal.domain.user.domain.repository.UserRepository;

import com.internalemployeeportal.global.config.security.token.UserPrincipal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Slf4j
@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String accountId) throws UsernameNotFoundException {
        log.debug("Loading user details for accountId={}", accountId);
        User user = userRepository.findByAccountId(accountId)
                .orElseThrow(() -> new UsernameNotFoundException("유저 정보를 찾을 수 없습니다."));

        log.debug("User found: accountId={}, role={}", user.getAccountId(), user.getRole());
        return UserPrincipal.create(user);
    }

}