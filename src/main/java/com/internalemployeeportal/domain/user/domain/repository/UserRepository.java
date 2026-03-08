package com.internalemployeeportal.domain.user.domain.repository;

import com.internalemployeeportal.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByAccountId(String accountId);

    boolean existsByAccountId(String accountId);
}
