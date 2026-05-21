package com.querypulse.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

import com.querypulse.backend.enums.Role;
import com.querypulse.backend.enums.ApprovalStatus;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    private String lastName;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    private String phoneNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)

     @Builder.Default
private Role role = Role.USER;

   @Builder.Default
private Boolean isActive = true;

@Builder.Default
private Boolean isEmailVerified = false;

@Builder.Default
private LocalDateTime createdAt = LocalDateTime.now();

@Builder.Default
private LocalDateTime updatedAt = LocalDateTime.now();

private String approvedBy;

private String rejectionReason;

@Enumerated(EnumType.STRING)
private ApprovalStatus approvalStatus =
        ApprovalStatus.PENDING;
}