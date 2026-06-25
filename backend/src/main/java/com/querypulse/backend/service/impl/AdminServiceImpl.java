package com.querypulse.backend.service.impl;

import com.querypulse.backend.entity.User;
import com.querypulse.backend.enums.ApprovalStatus;
import com.querypulse.backend.enums.Role;
import com.querypulse.backend.repository.UserRepository;
import com.querypulse.backend.service.AdminService;
import com.querypulse.backend.service.EmailService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl
implements AdminService {

    private final UserRepository userRepository;

    private final EmailService emailService;

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
    public String approveUser(
            UUID userId,
            String role,
            String message
    ) {

        User user = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );

        user.setApprovalStatus(ApprovalStatus.APPROVED);
        user.setIsActive(true);
        user.setApprovedAt(LocalDateTime.now());

        if (role != null && !role.isEmpty()) {
            try {
                user.setRole(Role.valueOf(role.toUpperCase()));
            } catch (IllegalArgumentException e) {
                user.setRole(Role.USER);
            }
        } else {
            user.setRole(Role.USER);
        }

        userRepository.save(user);

        String approvalMessage = message != null && !message.isEmpty()
                ? message
                : "Your account has been approved! You can now access QueryPulse.";

        emailService.sendApprovalEmail(
                user.getEmail(),
                user.getUsername(),
                true,
                approvalMessage,
                user.getRole().toString()
        );

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

        user.setApprovalStatus(ApprovalStatus.REJECTED);
        user.setRejectionReason(reason != null ? reason : "No reason provided");
        user.setIsActive(false);

        userRepository.save(user);

        String rejectionMessage = reason != null && !reason.isEmpty()
                ? reason
                : "Your access request has been rejected.";

        emailService.sendApprovalEmail(
                user.getEmail(),
                user.getUsername(),
                false,
                rejectionMessage,
                null
        );

        return "User rejected successfully";
    }
}