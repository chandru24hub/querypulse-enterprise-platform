package com.querypulse.backend.controller;

import com.querypulse.backend.entity.User;
import com.querypulse.backend.service.AdminService;
import com.querypulse.backend.dto.ApprovalRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    private final AdminService adminService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending-users")
    public List<User> getPendingUsers() {

        return adminService.getPendingUsers();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/approved-users")
    public List<User> getApprovedUsers() {

        return adminService.getApprovedUsers();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/rejected-users")
    public List<User> getRejectedUsers() {

        return adminService.getRejectedUsers();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/approve-user/{userId}")
    public String approveUser(

            @PathVariable UUID userId,

            @RequestBody ApprovalRequest request
    ) {

        return adminService.approveUser(
                userId,
                request.getRole(),
                request.getMessage()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/reject-user/{userId}")
    public String rejectUser(

            @PathVariable UUID userId,

            @RequestBody ApprovalRequest request
    ) {

        return adminService.rejectUser(
                userId,
                request.getMessage()
        );
    }
}