package com.querypulse.backend.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/db")
public class DbEngineerController {

    @PreAuthorize("hasRole('DB_ENGINEER')")
    @GetMapping("/metrics")
    public String databaseMetrics() {

        return "Database Metrics Access Granted";
    }
}