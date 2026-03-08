package com.internalemployeeportal.global.config.security.token;

import com.internalemployeeportal.domain.auth.application.CustomTokenProviderService;
import com.internalemployeeportal.domain.user.application.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Slf4j
@RequiredArgsConstructor
@Component
public class CustomOncePerRequestFilter extends OncePerRequestFilter {

    private final CustomTokenProviderService customTokenProviderService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String jwt = getJwtFromRequest(request);

        if (StringUtils.hasText(jwt) && customTokenProviderService.validateToken(jwt)) {

            // 1. JWT에서 accountId 추출
            String accountId = customTokenProviderService.getAccountIdFromToken(jwt);

            // 2. 사용자 상태 검증 (ACTIVE + 퇴사 여부 체크)
            userService.findActiveUser(accountId);

            // 3. Authentication 생성
            UsernamePasswordAuthenticationToken authentication =
                    customTokenProviderService.getAuthenticationByAccountId(jwt);

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            // 4. SecurityContext에 인증 정보 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}