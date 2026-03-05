package com.internalemployeeportal.domain.backgroundcheck.domain.repository;

import com.internalemployeeportal.domain.backgroundcheck.domain.BackgroundCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BackgroundCheckRepository extends JpaRepository<BackgroundCheck, Long> {
}
