package com.internalemployeeportal.domain.auth.application;

import com.internalemployeeportal.global.config.security.util.JwtTokenUtil;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;


@Slf4j
@RequiredArgsConstructor
@Service
public class CustomTokenProviderService {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;

    /**
     * JWT 유효성 검증
     */
    public boolean validateToken(String token) {
        return jwtTokenUtil.validateToken(token);
    }

    /**
     * JWT subject(accountId) 추출
     */
    public String getAccountIdFromToken(String token) {
        return jwtTokenUtil.getSubject(token);
    }

    /**
     * JWT로부터 Authentication 객체 생성
     */
    public UsernamePasswordAuthenticationToken getAuthenticationByAccountId(String token) {
        String accountId = getAccountIdFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(accountId);

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }
}