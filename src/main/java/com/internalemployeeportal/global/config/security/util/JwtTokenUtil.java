package com.internalemployeeportal.global.config.security.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
public class JwtTokenUtil {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private SecretKey getSigningKey() {
        // Secret Key를 바이트 배열로 변환하고 HMAC SHA-256 키 생성
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Map<String, Object> claims, String subject) { // subject는 accountId로 사용
        log.info("Generating JWT with subject [{}] and claims: {}", subject, claims);
        log.info("Using SHA-256 for signing");
        return Jwts.builder()
                .setClaims(claims) // 추가적인 payload
                .setSubject(subject) // accountId를 sub에 설정
                .setIssuedAt(new Date()) // 발급 시간 설정
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration)) // 만료 시간 설정
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Secret Key와 알고리즘 설정
                .compact();
    }

    public Claims getAllClaimsFromToken(String token) {
        try {
            log.info("Parsing JWT with SHA-256 and provided key");
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()) // 검증 시 동일한 Secret Key 사용
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("Failed to parse JWT: {}", e.getMessage());
            throw e;
        }
    }

    public boolean validateToken(String token) {
        try {

            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);

            return true;

        } catch (ExpiredJwtException e) {
            log.warn("JWT expired");
        } catch (JwtException e) {
            log.warn("Invalid JWT");
        }

        return false;
    }

    public String getUsernameFromJWT(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            String accountId = claims.getSubject(); // Subject에서 accountId 추출
            log.info("Extracted accountId [{}] from JWT", accountId);
            return accountId;
        } catch (Exception ex) {
            log.error("Failed to extract accountId from JWT: {}", ex.getMessage());
            throw ex;
        }
    }

    public String generateRefreshToken(Map<String, Object> claims, String subject) {
        log.info("Generating new JWT for subject: {}", subject);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject) // accountId를 sub에 설정
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration)) // Refresh Token의 만료 시간 설정
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }


    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }


    public Boolean validateRefreshToken(String token) {
        try {
            // Refresh Token의 유효성을 검사
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()) // Secret Key로 서명 확인
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Date expirationDate = claims.getExpiration();
            if (expirationDate.before(new Date())) {
                log.warn("Refresh Token has expired.");
                return false;
            }
            return true;
        } catch (Exception ex) {
            log.error("Invalid Refresh Token: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * Refresh Token에서 사용자 accountId를 추출하는 메서드
     * @param token JWT Refresh Token
     * @return 사용자 accountId - Refresh Token의 Subject에서 추출
     */
    // TODO: Refresh Token에서 accountId 추출 시 예외 처리 강화 필요 (예: 토큰이 유효하지 않거나 만료된 경우)
    public String getUsernameFromRefreshToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()) // Secret Key로 서명 확인
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String accountId = claims.getSubject(); // Subject에서 accountId 추출
            log.info("Extracted accountId [{}] from Refresh Token", accountId);
            return accountId;
        } catch (Exception ex) {
            log.error("Failed to extract accountId from Refresh Token: {}", ex.getMessage());
            throw ex; // 필요에 따라 커스텀 예외로 처리 가능
        }
    }

    public String getSubject(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            String subject = claims.getSubject();
            log.info("Extracted subject [{}] from JWT", subject);
            return subject;
        } catch (Exception ex) {
            log.error("Failed to extract subject from JWT: {}", ex.getMessage());
            throw ex;
        }
    }

    public Object getAccessTokenExpiration() {
        return accessTokenExpiration;
    }
}

