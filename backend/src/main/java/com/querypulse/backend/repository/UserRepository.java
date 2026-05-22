package com.querypulse.backend.repository;

import com.querypulse.backend.entity.User;
import com.querypulse.backend.enums.ApprovalStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository
        extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(
            String email
    );

    boolean existsByEmail(
            String email
    );

    boolean existsByUsername(
            String username
    );

    List<User> findByApprovalStatus(
            ApprovalStatus approvalStatus
    );
}