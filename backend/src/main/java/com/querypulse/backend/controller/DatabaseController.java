package com.querypulse.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.querypulse.backend.dto.CreateDatabaseRequest;
import com.querypulse.backend.dto.DatabaseResponse;
import com.querypulse.backend.entity.MonitoredDatabase;
import com.querypulse.backend.service.DatabaseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/databases")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class DatabaseController {

    private final DatabaseService
            databaseService;

    @PostMapping
    public MonitoredDatabase createDatabase(

            @RequestBody
            CreateDatabaseRequest request
    ) {

        return databaseService
                .createDatabase(request);
    }

    @GetMapping
    public List<DatabaseResponse>
    getAllDatabases() {

        return databaseService
                .getAllDatabases();
    }
}