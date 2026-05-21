package com.querypulse.backend.controller;

import com.querypulse.backend.entity.User;
import com.querypulse.backend.service.AdminService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard")
    public String adminDashboard() {

        return "Welcome Admin";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending-users")
    public List<User> getPendingUsers() {

        return adminService.getPendingUsers();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/approve/{userId}")
    public String approveUser(
            @PathVariable UUID userId
    ) {

        adminService.approveUser(userId);

        return "User approved successfully";
    }
}