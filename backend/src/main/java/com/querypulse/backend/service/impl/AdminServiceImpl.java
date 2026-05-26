package com.querypulse.backend.service.impl;

import com.querypulse.backend.entity.User;
import com.querypulse.backend.enums.ApprovalStatus;
import com.querypulse.backend.repository.UserRepository;
import com.querypulse.backend.service.AdminService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl
implements AdminService {

    private final UserRepository userRepository;

    @Override
    public List<User> getPendingUsers() {

        return userRepository.findByApprovalStatus(
                ApprovalStatus.PENDING
        );
    }

    @Override
    public List<User> getApprovedUsers() {

        return userRepository.findByApprovalStatus(
                ApprovalStatus.APPROVED
        );
    }

    @Override
    public List<User> getRejectedUsers() {

        return userRepository.findByApprovalStatus(
                ApprovalStatus.REJECTED
        );
    }

    @Override
    public String approveUser(UUID userId) {

        User user = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );

        user.setApprovalStatus(
                ApprovalStatus.APPROVED
        );

        user.setIsActive(true);

        userRepository.save(user);

        return "User approved successfully";
    }

    @Override
    public String rejectUser(
            UUID userId,
            String reason
    ) {

        User user = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );

        user.setApprovalStatus(
                ApprovalStatus.REJECTED
        );

        user.setRejectionReason(reason);

        user.setIsActive(false);

        userRepository.save(user);

        return "User rejected successfully";
    }
}