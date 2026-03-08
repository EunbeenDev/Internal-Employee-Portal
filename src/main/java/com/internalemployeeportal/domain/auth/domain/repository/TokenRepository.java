package com.internalemployeeportal.domain.auth.domain.repository;

import com.internalemployeeportal.domain.auth.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Token findByAccountId(String accountId);
    Optional<Token> findByRefreshToken(String refreshToken);

}