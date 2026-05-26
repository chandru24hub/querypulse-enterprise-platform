package com.querypulse.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.querypulse.backend.entity.User;
import com.querypulse.backend.enums.ApprovalStatus;

@Repository
public interface UserRepository
extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    List<User> findByApprovalStatus(
            ApprovalStatus approvalStatus
    );
}